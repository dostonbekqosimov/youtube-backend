package dasturlash.uz.service.video;

import dasturlash.uz.dto.request.video.*;
import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.TagResponseDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.video.*;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.entity.Tag;
import dasturlash.uz.entity.VideoTag;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.mapper.AdminVideoProjection;
import dasturlash.uz.mapper.VideoShortInfoProjection;
import dasturlash.uz.repository.VideoRepository;
import dasturlash.uz.repository.VideoTagRepository;
import dasturlash.uz.service.AttachService;
import dasturlash.uz.service.CategoryService;
import dasturlash.uz.service.ChannelService;
import dasturlash.uz.service.TagService;
import dasturlash.uz.util.VideoInfoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserRole;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    @Value("${app.domain}")
    private String domain;

    private final VideoRepository videoRepository;
    private final ChannelService channelService;
    private final AttachService attachService;
    private final CategoryService categoryService;
    private final VideoInfoMapper videoInfoMapper;
    private final TagService tagService;
    private final VideoTagRepository videoTagRepository;


    public VideoCreateResponseDTO createVideo(VideoCreateDTO dto) {
        log.info("Entering createVideo with request: {}", dto);

        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setCategoryId(dto.getCategoryId());
        video.setPlaylistId(dto.getPlaylistId());
        video.setAttachId(dto.getAttachId());
        video.setPreviewAttachId(dto.getPreviewAttachId());
        video.setDescription(dto.getDescription());
        video.setType(dto.getType());
        video.setChannelId(dto.getChannelId());
        video.setCreatedDate(LocalDateTime.now());

        // Set status and scheduled date
        updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());

        // Set other default values
        video.setVisible(true);
        video.setViewCount(0);
        video.setLikeCount(0);
        video.setDislikeCount(0);
        video.setSharedCount(0);

        video = videoRepository.save(video);
        log.info("Video created with ID: {}", video.getId());
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            List<Tag> tags = tagService.findOrCreateTags(dto.getTags());
            List<VideoTag> videoTags = createVideoTags(video, tags);
            videoTagRepository.saveAll(videoTags);
        }

        // Build and return response
        VideoCreateResponseDTO response = buildVideoCreateResponse(video);
        log.info("Exiting createVideo with response: {}", response);
        return response;
    }

    private List<VideoTag> createVideoTags(Video video, List<Tag> tags) {
        return tags.stream()
                .map(tag -> {
                    VideoTag videoTag = new VideoTag();
                    videoTag.setVideo(video);
                    videoTag.setTag(tag);
                    return videoTag;
                })
                .collect(Collectors.toList());
    }

    private VideoCreateResponseDTO buildVideoCreateResponse(Video video) {
        VideoCreateResponseDTO response = new VideoCreateResponseDTO();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setVideoLink(domain + "/api/videos/watch?v=" + video.getId());

        switch (video.getStatus()) {
            case PUBLIC -> {
                response.setPublic(true);
                response.setMessage("Video published");
                response.setAllowedSharePlatforms(List.of("Telegram", "WhatsApp", "Facebook", "X", "Email", "KakaoTalk", "Reddit"));
                response.setScheduledDate(null);
                response.setPublishedDate(video.getPublishedDate());
            }
            case PRIVATE -> {
                response.setPublic(false);
                response.setMessage("Only you can view this video");
                response.setAllowedSharePlatforms(Collections.emptyList());
            }
            case DRAFT -> {
                response.setPublic(false);
                response.setMessage("Video saved as draft");
                response.setAllowedSharePlatforms(Collections.emptyList());
            }
            case SCHEDULED -> {
                response.setPublic(false);
                response.setMessage("Video scheduled for " +
                        video.getScheduledDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a")));
                response.setScheduledDate(video.getScheduledDate());
                response.setAllowedSharePlatforms(Collections.emptyList());
            }
        }

        return response;
    }

    public VideoFullInfoDTO getVideoById(String videoId) {
        // Fetch the video entity
        Video video = getVideoEntityById(videoId);

        // If the video is visible or has PRIVATE status
        if (video.getVisible() || video.getStatus() == ContentStatus.PRIVATE) {

            // Check if the video is PRIVATE and ensure access is limited to owner or admin
            if (video.getStatus() == ContentStatus.PRIVATE) {
                // get only profile id here no need for whole channel [...]
                Channel channel = channelService.getById(video.getChannelId());
                Long currentUserId = getCurrentUserId();

                // Allow only if the current user is the owner or an admin
                if (!currentUserId.equals(channel.getProfileId()) && !isAdmin()) {
                    throw new ForbiddenException("Access to this video is restricted.");
                }
            }

            // Increment view count and save changes
            video.setViewCount(video.getViewCount() + 1);
            videoRepository.save(video);

            // Convert and return the video details
            VideoFullInfoDTO videoFullInfoDTO = toVideoFullInfoDTO(video);
            log.info("Returning video details for ID: {}", videoId);
            return videoFullInfoDTO;

        } else {
            throw new DataNotFoundException("Video not available or accessible.");
        }
    }

    private boolean isAdmin() {
        // Logic to determine if the user is an admin
        return getCurrentUserRole() == ProfileRole.ROLE_ADMIN;
    }

    @Transactional
    public VideoUpdateDTO updateVideo(String videoId, VideoUpdateDTO dto) {
        log.info("Updating video with ID: {} and request: {}", videoId, dto);

        Video video = getVideoAndCheckOwnership(videoId);

        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            video.setCategoryId(dto.getCategoryId());
        }

        if (dto.getPlaylistId() != null) {
            video.setPlaylistId(dto.getPlaylistId());
        }

        if (dto.getPreviewAttachId() != null) {
            video.setPreviewAttachId(dto.getPreviewAttachId());
        }

        if (dto.getType() != null) {
            video.setType(dto.getType());
        }

        if (dto.getStatus() != null) {
            // If video is scheduled, set scheduled date
            log.info("Updating status and dates for video ID: {} with request: {}", videoId, dto);
            updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());
        }

        if (dto.getTags() != null) {
            updateVideoTags(video, dto.getTags());
        }

        video.setUpdatedDate(LocalDateTime.now());

        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(videoRepository.save(video));
        log.info("Video updated with response: {}", updatedVideo);
        return updatedVideo;
    }

    private void updateVideoTags(Video video, List<String> newTagNames) {
        // Validate tag count
        final int MAX_TAGS = 10;
        if (newTagNames.size() > MAX_TAGS) {
            throw new AppBadRequestException("Too many tags. Maximum allowed: " + MAX_TAGS);
        }

        // Remove duplicates and normalize
        List<String> uniqueTagNames = newTagNames.stream()
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        // Clear existing tags
        video.getVideoTags().clear();

        // Find or create tags using TagService
        List<Tag> tags = tagService.findOrCreateTags(uniqueTagNames);

        // Create video tag relationships
        for (Tag tag : tags) {
            VideoTag videoTag = new VideoTag();
            videoTag.setVideo(video);
            videoTag.setTag(tag);

            video.getVideoTags().add(videoTag);
        }
    }

    private void updateVideoStatusAndDates(Video video, ContentStatus status, LocalDateTime scheduledDate) {

        if (status == ContentStatus.SCHEDULED) {
            validateScheduledVideo(scheduledDate);
            video.setStatus(ContentStatus.SCHEDULED);
            video.setPublishedDate(null);
            video.setScheduledDate(scheduledDate);

        } else if (status != null) {
            video.setStatus(status);

            if (status == ContentStatus.PUBLIC) {
                video.setPublishedDate(LocalDateTime.now());
                video.setScheduledDate(null);

            } else if (status == ContentStatus.PRIVATE) {
                video.setStatus(ContentStatus.PRIVATE);
                video.setPublishedDate(null);
                video.setScheduledDate(null);

            } else if (status == ContentStatus.DRAFT) {
                video.setStatus(ContentStatus.DRAFT);
                video.setPublishedDate(null);
                video.setScheduledDate(null);

            } else {
                video.setStatus(ContentStatus.PRIVATE);
                video.setPublishedDate(null);
                video.setScheduledDate(null);
            }
        }
    }

    private void validateScheduledVideo(LocalDateTime scheduledDate) {
        log.info("Validating scheduled video with scheduled date: {}", scheduledDate);

        if (scheduledDate == null) {
            log.error("Scheduled date is missing for scheduled video");
            throw new AppBadRequestException("Scheduled date is required for scheduled videos");
        }
    }

    @Transactional
    public VideoUpdateDTO updateStatus(String videoId, VideoStatusDTO dto) {
        log.info("Updating status for video ID: {} with request: {}", videoId, dto);

        Video video = getVideoAndCheckOwnership(videoId);

        // Set scheduled date using method
        updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());

        video.setUpdatedDate(LocalDateTime.now());

        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(videoRepository.save(video));
        log.info("Status updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoUpdateDTO updatePlaylist(String videoId, VideoPlaylistDTO dto) {
        log.info("Updating playlist for video ID: {} with request: {}", videoId, dto);

        Video video = getVideoAndCheckOwnership(videoId);

        video.setPlaylistId(dto.getPlaylistId());
        video.setUpdatedDate(LocalDateTime.now());

        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(videoRepository.save(video));
        log.info("Playlist updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoUpdateDTO updateCategory(String videoId, VideoCategoryDTO dto) {
        log.info("Updating category for video ID: {} with request: {}", videoId, dto);

        Video video = getVideoAndCheckOwnership(videoId);

        video.setCategoryId(dto.getCategoryId());
        video.setUpdatedDate(LocalDateTime.now());

        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(videoRepository.save(video));
        log.info("Category updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoUpdateDTO updatePreview(String videoId, VideoPreviewDTO dto) {
        log.info("Updating preview for video ID: {} with request: {}", videoId, dto);

        Video video = getVideoAndCheckOwnership(videoId);

        video.setPreviewAttachId(dto.getPreviewAttachId());
        video.setUpdatedDate(LocalDateTime.now());

        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(videoRepository.save(video));
        log.info("Preview updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    private Video getVideoAndCheckOwnership(String videoId) {
        log.info("Checking ownership for video ID: {}", videoId);

        Video video = getVideoEntityById(videoId);

        Channel channel = channelService.getById(video.getChannelId());
        if (!channel.getProfileId().equals(getCurrentUserId())) {
            log.error("User does not have permission to update video ID: {}", videoId);
            throw new AppBadRequestException("You don't have permission to update this video");
        }

        log.info("Ownership verified for video ID: {}", videoId);
        return video;
    }

    private VideoFullInfoDTO toVideoFullInfoDTO(Video video) {
        log.debug("Converting video entity to DTO for video ID: {}", video.getId());

        VideoFullInfoDTO videoFullInfoDTO = new VideoFullInfoDTO();
        videoFullInfoDTO.setId(video.getId());
        videoFullInfoDTO.setTitle(video.getTitle());
        videoFullInfoDTO.setDescription(video.getDescription());

        // Get media URLs
        log.debug("Fetching media URLs for video ID: {}", video.getId());

        MediaUrlDTO previewAttach = attachService.getUrlOfMedia(video.getPreviewAttachId());
        log.debug("Retrieved preview attachment for video ID: {}, preview ID: {}", video.getId(), video.getPreviewAttachId());

        VideoMediaDTO videoAttach = attachService.getUrlOfVideo(video.getAttachId());
        log.debug("Retrieved video attachment for video ID: {}, attachment ID: {}", video.getId(), video.getAttachId());

        // Set media URLs
        videoFullInfoDTO.setPreviewAttach(previewAttach);
        videoFullInfoDTO.setVideoAttach(videoAttach);

        // Get category short info by using category id
        log.debug("Fetching category info for video ID: {}, category ID: {}", video.getId(), video.getCategoryId());
        CategoryResponseDTO category = categoryService.getCategoryShortInfoById(video.getCategoryId());

        // Set category short info
        videoFullInfoDTO.setCategory(category);

        // Set tags
        log.debug("Getting tag list for video ID: {}", video.getTitle());
        List<TagResponseDTO> tagResponseDTOs = new ArrayList<>();
        for (VideoTag videoTag : video.getVideoTags()) {
            TagResponseDTO tagDTO = new TagResponseDTO();
            tagDTO.setId(videoTag.getTag().getId());
            tagDTO.setName(videoTag.getTag().getName());
            tagResponseDTOs.add(tagDTO);
        }
        log.debug("Setting tag list for video ID: {}", video.getTitle());
        videoFullInfoDTO.setTags(tagResponseDTOs);

        // Get channel by using channel id
        log.debug("Fetching channel info for video ID: {}, channel ID: {}", video.getId(), video.getChannelId());
        VideoChannelDTO channel = channelService.getVideoChannelShortInfo(video.getChannelId());

        // Set channel
        videoFullInfoDTO.setChannel(channel);

        // Setting like details
        log.debug("Setting like details for video ID: {}, likes: {}, dislikes: {}", video.getId(), video.getLikeCount(), video.getDislikeCount());

        VideoLikeDTO likeDetails = new VideoLikeDTO();
        likeDetails.setLikeCount(video.getLikeCount());
        likeDetails.setDislikeCount(video.getDislikeCount());

        // Set like details
        likeDetails.setIsUserLiked(Boolean.FALSE);
        likeDetails.setIsUserDisliked(Boolean.FALSE);
        videoFullInfoDTO.setLikeDetails(likeDetails);

        videoFullInfoDTO.setViewCount(video.getViewCount());
        videoFullInfoDTO.setSharedCount(video.getSharedCount());
        videoFullInfoDTO.setStatus(video.getStatus());


        videoFullInfoDTO.setUpdatedDate(video.getUpdatedDate());
        videoFullInfoDTO.setScheduledDate(video.getScheduledDate());
        videoFullInfoDTO.setCreatedDate(video.getCreatedDate());
        videoFullInfoDTO.setPublishedDate(video.getPublishedDate());


        log.debug("Completed converting video entity to DTO for video ID: {}", video.getId());
        return videoFullInfoDTO;
    }

    private VideoUpdateDTO toVideoUpdateDTO(Video video) {
        VideoUpdateDTO videoUpdateDTO = new VideoUpdateDTO();

        videoUpdateDTO.setTitle(video.getTitle());
        videoUpdateDTO.setDescription(video.getDescription());
        videoUpdateDTO.setCategoryId(video.getCategoryId());
        videoUpdateDTO.setPlaylistId(video.getPlaylistId());
        videoUpdateDTO.setPreviewAttachId(video.getPreviewAttachId());
        videoUpdateDTO.setType(video.getType());
        videoUpdateDTO.setStatus(video.getStatus());

        // setting video tags
        List<String> tagNames = video.getVideoTags().stream()
                .map(videoTag -> videoTag.getTag().getName())
                .collect(Collectors.toList());
        videoUpdateDTO.setTags(tagNames);

        videoUpdateDTO.setScheduledDate(video.getScheduledDate());
        videoUpdateDTO.setUpdatedDate(video.getUpdatedDate());

        return videoUpdateDTO;
    }

    private Video getVideoEntityById(String videoId) {
        log.info("Fetching video with ID: {}", videoId);

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> {
                    log.error("Video not found with ID: {}", videoId);
                    return new DataNotFoundException("Video not found");
                });

        log.info("Successfully fetched video: {}", video);
        return video;
    }

    public PageImpl<VideoShortInfoDTO> getVideoListByCategoryId(int page, int size, Long categoryId) {
        log.info("Fetching video list by category ID: {}, page: {}, size: {}", categoryId, page, size);

        Pageable pageRequest = PageRequest.of(page, size);

        // Fetch projections from the database
        Page<VideoShortInfoProjection> projections = videoRepository.findPublicVideosByCategoryId(categoryId, pageRequest);
        log.debug("Fetched {} videos for category ID: {}", projections.getContent().size(), categoryId);

        // Map projections to DTOs
        List<VideoShortInfoDTO> response = projections.stream()
                .map(videoInfoMapper::toVideShortInfoDTO)
                .toList();

        log.info("Mapped {} videos to VideoShortInfoDTO for category ID: {}", response.size(), categoryId);
        return new PageImpl<>(response, pageRequest, projections.getTotalElements());
    }

    public PageImpl<VideoShortInfoDTO> getVideoListByTitle(int page, int size, String title) {
        log.info("Fetching video list by title: '{}', page: {}, size: {}", title, page, size);

        Pageable pageRequest = PageRequest.of(page, size);

        // Fetch projections from the database
        Page<VideoShortInfoProjection> projections = videoRepository.findPublicVideosByTitle(title, pageRequest);
        log.debug("Fetched {} videos matching title: '{}'", projections.getContent().size(), title);

        // Map projections to DTOs
        List<VideoShortInfoDTO> response = projections.stream()
                .map(videoInfoMapper::toVideShortInfoDTO)
                .toList();

        log.info("Mapped {} videos to VideoShortInfoDTO for title: '{}'", response.size(), title);
        return new PageImpl<>(response, pageRequest, projections.getTotalElements());
    }

    public PageImpl<VideoShortInfoDTO> getVideoListByTagName(String tagName, int page, int size) {
        // Create pageable request
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Fetch videos by tag name
        log.info("Fetching video list by tag: '{}', page: {}, size: {}", tagName, page, size);
        Page<VideoShortInfoProjection> videoPage = videoRepository.findVideosByTagName(tagName, pageable);

        // Convert to DTO
        List<VideoShortInfoDTO> videoDTOs = videoPage.getContent().stream()
                .map(videoInfoMapper::toVideShortInfoDTO)
                .collect(Collectors.toList());

        // Return as PageImpl
        log.warn("Returning videos based on tag: '{}'", tagName);
        return new PageImpl<>(videoDTOs, pageable, videoPage.getTotalElements());
    }

    public PageImpl<VideoPlayListInfoDTO> getChannelVideoListByChannelId(int page, int size, String channelId) {
        log.info("Fetching channel video list for channel ID: {}, page: {}, size: {}", channelId, page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.by("publishedDate").descending());

        // Fetch projections from the database
        Page<VideoShortInfoProjection> videos = videoRepository.findPublicChannelVideosListByChannelId(channelId, pageRequest);
        log.debug("Fetched {} videos for channel ID: {}", videos.getContent().size(), channelId);

        // Map projections to DTOs
        List<VideoPlayListInfoDTO> response = videos.stream()
                .map(videoInfoMapper::videoPlayListInfoDTODTO)
                .toList();

        log.info("Mapped {} videos to VideoPlayListInfoDTO for channel ID: {}", response.size(), channelId);
        return new PageImpl<>(response, pageRequest, videos.getTotalElements());
    }

    public Page<AdminVideoInfoDTO> getAdminVideoList(int page, int size) {
        log.info("Fetching admin video list, page: {}, size: {}", page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Fetch projections from the database
        Page<AdminVideoProjection> videos = videoRepository.findAdminVideoInfo(pageRequest);
        log.debug("Fetched {} videos for admin list.", videos.getContent().size());

        // Map projections to DTOs
        List<AdminVideoInfoDTO> response = videos.stream()
                .map(videoInfoMapper::toAdminVideoInfoDTO)
                .toList();

        log.info("Mapped {} videos to AdminVideoInfoDTO.", response.size());
        return new PageImpl<>(response, pageRequest, videos.getTotalElements());
    }

    // need to be optimized, use projection later
    public VideoUpdateDTO getUpdateInfo(String videoId) {
        Video video = getVideoAndCheckOwnership(videoId);

        return toVideoUpdateDTO(video);

    }
}
