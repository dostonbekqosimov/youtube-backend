package dasturlash.uz.service.video;
import dasturlash.uz.dto.PlaylistVideoDTO;
import dasturlash.uz.dto.request.video.*;
import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.TagResponseDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.video.*;
import dasturlash.uz.dto.response.video.like.VideoLikeDTO;
import dasturlash.uz.entity.Tag;
import dasturlash.uz.entity.VideoTag;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.exceptions.SomethingWentWrongException;
import dasturlash.uz.mapper.*;
import dasturlash.uz.repository.VideoRepository;
import dasturlash.uz.service.*;
import dasturlash.uz.util.UserInfoUtil;
import dasturlash.uz.util.CustomProjectionMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserRole;
import static dasturlash.uz.security.SpringSecurityUtil.currentUserInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    @Value("${app.domain}")
    private String domain;
    private final VideoWatchedService videoWatchedService;
    private final VideoRepository videoRepository;
    private final ChannelService channelService;
    private final AttachService attachService;
    private final CategoryService categoryService;
    private final CustomProjectionMapper customProjectionMapper;
    private final TagService tagService;
    private final VideoTagService videoTagService;
    private final VideoRecordService videoRecordService;
    private final PlaylistVideoService playlistVideoService;

    public String hello() {
        return "hello";
    }


    public VideoCreateResponseDTO createVideo(VideoCreateDTO dto) {
        log.info("Entering createVideo with request: {}", dto);

        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setCategoryId(dto.getCategoryId());
//        video.setPlaylistId(dto.getPlaylistId());

        // video bilan previewlar db da attach table da bo'lishi kerak
        // bu bo'ladi albatta lekin extra checking
        if (!attachService.validateAttachment(dto.getAttachId())) {
            throw new AppBadRequestException("Invalid video attachment");
        }

        // Validate preview attachment (if provided)
        if (dto.getPreviewAttachId() != null &&
                !attachService.validateAttachment(dto.getPreviewAttachId())) {
            throw new AppBadRequestException("Invalid preview attachment");
        }

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

            videoTagService.createNewVideoTags(video, tags);
        }

        // Build and return response
        VideoCreateResponseDTO response = buildVideoCreateResponse(video);
        // Create playlistVideo call service create method

//         for (String playlistId : dto.getPlaylistId()) {

//             PlaylistVideoDTO dto1 = new PlaylistVideoDTO();
//             dto1.setPlaylistId(playlistId);
//             dto1.setVideoId(video.getId());
//             playlistVideoService.create(dto1);
//         }


        log.info("Exiting createVideo with response: {}", response);
        return response;
    }

    private VideoCreateResponseDTO buildVideoCreateResponse(Video video) {
        VideoCreateResponseDTO response = new VideoCreateResponseDTO();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setVideoLink(generateVideoWatchUrl(video.getId()));

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
                response.setMessage("Video scheduled for " + video.getScheduledDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a")));
                response.setScheduledDate(video.getScheduledDate());
                response.setAllowedSharePlatforms(Collections.emptyList());
            }
        }

        return response;
    }

    public VideoFullInfoDTO getVideoById(String videoId, HttpServletRequest request) {
        UserInfoUtil userInfoUtil1 = new UserInfoUtil();
        String ipAddress = getUserIP(request);

        Long currentUserId = null;
        try {
            currentUserId = getCurrentUserId();
        } catch (Exception e) {
            // If authentication fails, currentUserId will remain null
            log.warn("No authenticated user found when getting video");
        }
        Video video = getVideoEntityById(videoId);
        UserInfoUtil userInfoUtil = currentUserInfo(request);

        // If the video is visible or has PRIVATE status
        if (video.getVisible() || video.getStatus() == ContentStatus.PRIVATE) {

            // Check if the video is PRIVATE and ensure access is limited to owner or admin
            if (video.getStatus() == ContentStatus.PRIVATE) {
                // get only profile id here no need for whole channel [...]
                Long videoOwnerId = channelService.getChannelOwnerId(video.getChannelId());


                // Allow only if the current user is the owner or an admin
                if (!currentUserId.equals(videoOwnerId) || !isAdmin()) {
                    throw new ForbiddenException("Access to this video is restricted.");
                }
            }

            // Increment view count and save changes
            videoRecordService.addViewRecord(videoId, ipAddress, currentUserId);

            videoRepository.save(video);

            // Convert and return the video details
            VideoFullInfoDTO videoFullInfoDTO = toVideoFullInfoDTO(video);


            videoWatchedService.addHistoryWatch(videoId, userInfoUtil, request);
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

        // Only update title if new title is different
        if (dto.getTitle() != null && !dto.getTitle().equals(video.getTitle())) {
            video.setTitle(dto.getTitle());
        }

        // Only update description if new description is different
        if (dto.getDescription() != null && !dto.getDescription().equals(video.getDescription())) {
            video.setDescription(dto.getDescription());
        }

        // Only update category if new category is different
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(video.getCategoryId())) {
            video.setCategoryId(dto.getCategoryId());
        }

        /*// Only update playlist if new playlist is different
        if (dto.getNewPlaylistIds() != null && !dto.getNewPlaylistIds().equals(video.getPlaylistId())) {
            video.setPlaylistId(dto.getPlaylistId());
            System.out.println("Playlist should be updated...");
        }*/

        // Only update preview attach if new preview is different
        if (dto.getPreviewAttachId() != null && !dto.getPreviewAttachId().equals(video.getPreviewAttachId())) {
            video.setPreviewAttachId(dto.getPreviewAttachId());
        }

        // Only update type if new type is different
        if (dto.getType() != null && dto.getType() != video.getType()) {
            video.setType(dto.getType());
        }

        // Handle status and scheduled date
        if (dto.getStatus() != null) {
            log.info("Updating status and dates for video ID: {} with request: {}", videoId, dto);
            updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());
        }

        if (dto.getTags() != null) {
            // Fetch existing tags from the database
            List<String> currentTags = videoTagService.getVisibleTagNamesForVideo(video);

            // Convert both lists to sets for comparison
            Set<String> existingTagSet = new HashSet<>(currentTags);
            Set<String> incomingTagSet = new HashSet<>(dto.getTags());

            // Check if tags have changed
            if (!existingTagSet.equals(incomingTagSet)) {
                if (dto.getTags().isEmpty()) {
                    // If tags is an empty list, remove all existing tags
                    videoTagService.updateVideoTags(video, Collections.emptyList());
                } else {
                    // If tags are provided, create or find tags and update
                    List<Tag> tags = tagService.findOrCreateTags(dto.getTags());
                    videoTagService.updateVideoTags(video, tags);
                }
            } else {
                log.info("Tags are unchanged; skipping tag update process.");
            }
        }


        video.setUpdatedDate(LocalDateTime.now());

        Video video1 = videoRepository.save(video);
        VideoUpdateDTO updatedVideo = toVideoUpdateDTO(video1);
        log.info("Video updated with response: {}", updatedVideo);
        return updatedVideo;
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

        //delete user selected playlist
        int i = playlistVideoService.deleteSelectedPlaylist(videoId, dto.getRemovePlaylistIds());
        log.info("size: {}", i);

        //update new selected playlists
        PlaylistVideoDTO playlistVideoDTO = new PlaylistVideoDTO();
        for (String newPlaylistId : dto.getNewPlaylistIds()) {
            playlistVideoDTO.setVideoId(videoId);
            playlistVideoDTO.setPlaylistId(newPlaylistId);
            playlistVideoDTO.setOrderNumber(null);
            playlistVideoService.create(playlistVideoDTO);
        }


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

        Long videoOwnerId = channelService.getChannelOwnerId(video.getChannelId());
        if (!videoOwnerId.equals(getCurrentUserId())) {
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
        videoUpdateDTO.setNewPlaylistIds(Collections.emptyList());
        videoUpdateDTO.setPreviewAttachId(video.getPreviewAttachId());
        videoUpdateDTO.setType(video.getType());
        videoUpdateDTO.setStatus(video.getStatus());

        // Get the visible tag names using the VideoTagService
        List<String> tags = videoTagService.getVisibleTagNamesForVideo(video);
        videoUpdateDTO.setTags(tags);

        videoUpdateDTO.setScheduledDate(video.getScheduledDate());

        return videoUpdateDTO;
    }

    private Video getVideoEntityById(String videoId) {
        log.info("Fetching video with ID: {}", videoId);

        Video video = videoRepository.findByIdAndVisibleTrue(videoId).orElseThrow(() -> {
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
        List<VideoShortInfoDTO> response = projections.stream().map(customProjectionMapper::toVideShortInfoDTO).toList();

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
        List<VideoShortInfoDTO> response = projections.stream().map(customProjectionMapper::toVideShortInfoDTO).toList();

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
        List<VideoShortInfoDTO> videoDTOs = videoPage.getContent().stream().map(customProjectionMapper::toVideShortInfoDTO).collect(Collectors.toList());

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
        List<VideoPlayListInfoDTO> response = videos.stream().map(customProjectionMapper::videoPlayListInfoDTODTO).toList();

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
        List<AdminVideoInfoDTO> response = videos.stream().map(customProjectionMapper::toAdminVideoInfoDTO).toList();

        log.info("Mapped {} videos to AdminVideoInfoDTO.", response.size());
        return new PageImpl<>(response, pageRequest, videos.getTotalElements());
    }

    // need to be optimized, use projection later
    public VideoUpdateDTO getUpdateInfo(String videoId) {
        Video video = getVideoAndCheckOwnership(videoId);

        return toVideoUpdateDTO(video);

    }

    public VideoInfoInPlaylist getVideoInfoInPlaylist(String playlistId) {
        return videoRepository.findVideoInfoById(playlistId);
    }

    @Transactional
    public String deleteVideoById(String videoId) {
        if (videoId == null) {
            throw new AppBadRequestException("VideoId cannot be null or empty");
        }

        Video video = getVideoAndCheckOwnership(videoId);

        videoTagService.updateVideoTags(video, Collections.emptyList());

        boolean isAttachmentDeleted = attachService.deleteVideo(video.getAttachId(), video.getPreviewAttachId());

        Integer rows = videoRepository.changeVisibility(videoId, Boolean.FALSE);

        if (rows > 0 && isAttachmentDeleted) {
            log.info("Video with ID {} successfully soft-deleted", videoId);
            return "Video successfully deleted";
        } else {
            log.error("Error deleting video with ID {}: ", videoId);
            throw new SomethingWentWrongException("Failed to update video visibility");
        }


    }

    public static String getUserIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public VideoShareDto shareVideoById(String videoId, HttpServletRequest request) {

        String ipAddress = getUserIP(request);

        VideoShareProjection video = videoRepository.findVideoShareInfoById(videoId)
                .orElseThrow(() -> new DataNotFoundException("Video not found"));


        Long currentUserId = null;
        try {
            currentUserId = getCurrentUserId();
        } catch (Exception e) {
            // If authentication fails, currentUserId will remain null
            log.warn("No authenticated user found when sharing video");
        }


        videoRecordService.increaseShareCount(videoId, ipAddress, currentUserId);

        return customProjectionMapper.toVideoShareDto(video);

    }

    public String generateVideoWatchUrl(String videoId) {
        return domain + "/api/videos/watch?v=" + videoId;
    }

    public VideoWatchedHistory getVideoWatchedHistory(String videoId) {
        return videoRepository.findVideoById(videoId);
    }

    // need to add check and logs
    public List<VideoShortInfoDTO> getVideoShortInfoByVideoIds(List<String> videoIds) {
        List<VideoShortInfoProjection> projections = videoRepository.findVideosByVideoIds(videoIds);
        return projections.stream()
                .map(customProjectionMapper::toVideShortInfoDTO)
                .collect(Collectors.toList());
    }

}



