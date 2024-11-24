package dasturlash.uz.service;

import dasturlash.uz.dto.request.video.VideoCreateDTO;
import dasturlash.uz.dto.response.video.VideoCreateResponseDTO;
import dasturlash.uz.dto.response.video.VideoDTO;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.exceptions.UnauthorizedException;
import dasturlash.uz.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static dasturlash.uz.enums.ContentStatus.PUBLIC;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${app.domain}")
    private String domain;

    private final VideoRepository videoRepository;


    public VideoCreateResponseDTO createVideo(VideoCreateDTO dto) {

        // Create new video entity
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


        // handle status and published date
        if (dto.getStatus() == ContentStatus.SCHEDULED) {

            // First validate scheduled video if applicable
            validateScheduledVideo(dto);

            video.setStatus(ContentStatus.SCHEDULED);
            video.setPublishedDate(dto.getPublishedDate());  // Safe because validation ensures it's not null
        } else if (dto.getStatus() != null) {
            video.setStatus(dto.getStatus());
            if (dto.getStatus() == ContentStatus.PUBLIC) {
                video.setPublishedDate(LocalDateTime.now());
            }
        } else {
            video.setStatus(ContentStatus.PRIVATE);
        }

        // Set default values
        video.setStatus(dto.getStatus() != null ? dto.getStatus() : ContentStatus.PRIVATE);
        video.setCreatedDate(LocalDateTime.now());
        video.setVisible(true);
        video.setViewCount(0);
        video.setLikeCount(0);
        video.setDislikeCount(0);
        video.setSharedCount(0);

        // Save the video
        video = videoRepository.save(video);

        VideoCreateResponseDTO response = new VideoCreateResponseDTO();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setVideoLink(domain + "/api/videos/watch?v=" + video.getId());

        // Configure response based on status
        switch (video.getStatus()) {
            case PUBLIC -> {
                response.setPublic(true);
                response.setMessage("Video published");
                response.setAllowedSharePlatforms(List.of(
                        "Telegran",
                        "WhatsApp", "Facebook", "X", "Email",
                        "KakaoTalk", "Reddit"
                ));
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
                        video.getPublishedDate().format(
                                DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a")
                        ));
                response.setScheduledDate(video.getPublishedDate());
                response.setAllowedSharePlatforms(Collections.emptyList());
            }
        }

        return response;
    }

    private void validateScheduledVideo(VideoCreateDTO dto) {
        if (dto.getStatus() == ContentStatus.SCHEDULED) {
            if (dto.getPublishedDate() == null) {
                throw new AppBadRequestException("Published date is required for scheduled videos");
            }
            if (dto.getPublishedDate().isBefore(LocalDateTime.now())) {
                throw new AppBadRequestException("Published date must be in the future");
            }
        }
    }

    public VideoDTO getVideoById(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new DataNotFoundException("Video not found"));

        // Check visibility and permissions
        if (!video.getVisible() || video.getStatus() == ContentStatus.PRIVATE) {
            throw new ForbiddenException("Video is not accessible");
        }

        // Increment view count
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);

        return toDTO(video);
    }

    private VideoDTO toDTO(Video video) {
        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(video.getId());
        videoDTO.setTitle(video.getTitle());
        videoDTO.setDescription(video.getDescription());
        videoDTO.setPreviewUrl(domain + "/api/attach/open/" + video.getPreviewAttachId());
        videoDTO.setVideoUrl(domain + "/api/attach/open/" + video.getAttachId());
        videoDTO.setViewCount(video.getViewCount());
        videoDTO.setLikeCount(video.getLikeCount());
        videoDTO.setDislikeCount(video.getDislikeCount());
        videoDTO.setSharedCount(video.getSharedCount());
        videoDTO.setStatus(video.getStatus());
        videoDTO.setCreatedDate(video.getCreatedDate());
        videoDTO.setPublishedDate(video.getPublishedDate());
        return videoDTO;
    }


}
