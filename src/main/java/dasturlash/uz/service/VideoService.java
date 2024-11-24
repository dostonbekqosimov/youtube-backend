package dasturlash.uz.service;

import dasturlash.uz.dto.request.video.*;
import dasturlash.uz.dto.response.channel.MediaUrlDTO;
import dasturlash.uz.dto.response.video.VideoCreateResponseDTO;
import dasturlash.uz.dto.response.video.VideoFullInfoDTO;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.repository.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    @Value("${app.domain}")
    private String domain;

    private final VideoRepository videoRepository;
    private final ChannelService channelService;
    private final AttachService attachService;

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


        // set status and scheduled date
        updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());

        // Set other default values
        video.setVisible(true);
        video.setViewCount(0);
        video.setLikeCount(0);
        video.setDislikeCount(0);
        video.setSharedCount(0);

        video = videoRepository.save(video);
        log.info("Video created with ID: {}", video.getId());

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

        log.info("Exiting createVideo with response: {}", response);
        return response;
    }


    public VideoFullInfoDTO getVideoById(String videoId) {

        Video video = getVideoEntityById(videoId);

        if (!video.getVisible() || video.getStatus() == ContentStatus.PRIVATE) {
            log.warn("Video with ID: {} is not accessible", videoId);
            throw new ForbiddenException("Video is not accessible");
        }

        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);

        VideoFullInfoDTO videoFullInfoDTO = toDTO(video);
        log.info("Returning video details for ID: {}", videoId);
        return videoFullInfoDTO;
    }

    @Transactional
    public VideoFullInfoDTO updateVideo(String videoId, VideoUpdateDTO dto) {
        log.info("Updating video with ID: {} and request: {}", videoId, dto);
        Video video = getVideoAndCheckOwnership(videoId);

        if (dto.getTitle() != null) {
            video.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            video.setDescription(dto.getDescription());
        }
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
            video.setStatus(dto.getStatus());

            // If video is scheduled, set scheduled date
            updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());
        }


        video.setUpdatedDate(LocalDateTime.now());
        VideoFullInfoDTO updatedVideo = toDTO(videoRepository.save(video));
        log.info("Video updated with response: {}", updatedVideo);
        return updatedVideo;
    }

    private void updateVideoStatusAndDates(Video video, ContentStatus status, LocalDateTime scheduledDate) {

        if (status == ContentStatus.SCHEDULED) {
            validateScheduledVideo(scheduledDate);
            video.setStatus(ContentStatus.SCHEDULED);
            video.setScheduledDate(scheduledDate);
        } else if (status != null) {
            video.setStatus(status);
            if (status == ContentStatus.PUBLIC) {
                video.setPublishedDate(LocalDateTime.now());
            } else if (status == ContentStatus.PRIVATE) {
                video.setStatus(ContentStatus.PRIVATE);
                video.setPublishedDate(null); // Avoid setting a published date for private videos
                video.setScheduledDate(null);
            } else if (status == ContentStatus.DRAFT) {
                video.setStatus(ContentStatus.DRAFT);
                video.setPublishedDate(null); // Drafts are not published
                video.setScheduledDate(null);
            } else {
                video.setStatus(ContentStatus.PRIVATE);
                video.setPublishedDate(null); // Fallback to private, with no published date
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
    public VideoFullInfoDTO updateStatus(String videoId, VideoStatusDTO dto) {
        log.info("Updating status for video ID: {} with request: {}", videoId, dto);
        Video video = getVideoAndCheckOwnership(videoId);

        // set scheduled date using method
        updateVideoStatusAndDates(video, dto.getStatus(), dto.getScheduledDate());

        video.setUpdatedDate(LocalDateTime.now());
        VideoFullInfoDTO updatedVideo = toDTO(videoRepository.save(video));
        log.info("Status updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoFullInfoDTO updatePlaylist(String videoId, VideoPlaylistDTO dto) {
        log.info("Updating playlist for video ID: {} with request: {}", videoId, dto);
        Video video = getVideoAndCheckOwnership(videoId);
        video.setPlaylistId(dto.getPlaylistId());
        video.setUpdatedDate(LocalDateTime.now());
        VideoFullInfoDTO updatedVideo = toDTO(videoRepository.save(video));
        log.info("Playlist updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoFullInfoDTO updateCategory(String videoId, VideoCategoryDTO dto) {
        log.info("Updating category for video ID: {} with request: {}", videoId, dto);
        Video video = getVideoAndCheckOwnership(videoId);
        video.setCategoryId(dto.getCategoryId());
        video.setUpdatedDate(LocalDateTime.now());
        VideoFullInfoDTO updatedVideo = toDTO(videoRepository.save(video));
        log.info("Category updated for video ID: {} with response: {}", videoId, updatedVideo);
        return updatedVideo;
    }

    @Transactional
    public VideoFullInfoDTO updatePreview(String videoId, VideoPreviewDTO dto) {
        log.info("Updating preview for video ID: {} with request: {}", videoId, dto);
        Video video = getVideoAndCheckOwnership(videoId);
        video.setPreviewAttachId(dto.getPreviewAttachId());
        video.setUpdatedDate(LocalDateTime.now());
        VideoFullInfoDTO updatedVideo = toDTO(videoRepository.save(video));
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

    private VideoFullInfoDTO toDTO(Video video) {
        log.debug("Converting video entity to DTO for video ID: {}", video.getId());
        VideoFullInfoDTO videoFullInfoDTO = new VideoFullInfoDTO();
        videoFullInfoDTO.setId(video.getId());
        videoFullInfoDTO.setTitle(video.getTitle());
        videoFullInfoDTO.setDescription(video.getDescription());

        // get media urls
        MediaUrlDTO previewAttach = attachService.getUrlOfMedia(video.getPreviewAttachId());

        // setting duration null because preview attach doesn't need duration for now!
        previewAttach.setDuration(null);

        MediaUrlDTO videoAttach = attachService.getUrlOfMedia(video.getAttachId());

        // set media urls
        videoFullInfoDTO.setPreviewAttach(previewAttach);
        videoFullInfoDTO.setVideoAttach(videoAttach);

        videoFullInfoDTO.setViewCount(video.getViewCount());
        videoFullInfoDTO.setLikeCount(video.getLikeCount());
        videoFullInfoDTO.setDislikeCount(video.getDislikeCount());
        videoFullInfoDTO.setSharedCount(video.getSharedCount());
        videoFullInfoDTO.setStatus(video.getStatus());
        videoFullInfoDTO.setCreatedDate(video.getCreatedDate());
        videoFullInfoDTO.setPublishedDate(video.getPublishedDate());
        return videoFullInfoDTO;
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

}
