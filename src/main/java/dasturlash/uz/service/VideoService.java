package dasturlash.uz.service;

import dasturlash.uz.dto.request.video.VideoCreateDTO;
import dasturlash.uz.dto.response.video.VideoCreateResponseDTO;
import dasturlash.uz.dto.response.video.VideoDTO;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.exceptions.UnauthorizedException;
import dasturlash.uz.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
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
        video.setAttachId(dto.getAttachId());
        video.setPreviewAttachId(dto.getPreviewAttachId());
        video.setDescription(dto.getDescription());
        video.setType(dto.getType());
        video.setCreatedDate(LocalDateTime.now());

        // Handle status and published date
        if (dto.getStatus() == ContentStatus.SCHEDULED && dto.getPublishedDate() != null) {
            video.setStatus(ContentStatus.SCHEDULED);
            video.setPublishedDate(dto.getPublishedDate());
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


}
