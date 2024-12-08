package dasturlash.uz.util;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.OwnerInfoDTO;
import dasturlash.uz.dto.response.PlaylistInfoDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.comment.CommentOwnerInfo;
import dasturlash.uz.dto.response.video.AdminVideoInfoDTO;
import dasturlash.uz.dto.response.video.VideoPlayListInfoDTO;
import dasturlash.uz.dto.response.video.VideoShareDto;
import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
import dasturlash.uz.mapper.AdminVideoProjection;
import dasturlash.uz.mapper.CommentOwnerInfoProjection;
import dasturlash.uz.mapper.VideoShareProjection;
import dasturlash.uz.mapper.VideoShortInfoProjection;
import dasturlash.uz.service.AttachService;
import dasturlash.uz.service.ChannelService;
import dasturlash.uz.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomProjectionMapper {
    private static final Logger logger = LoggerFactory.getLogger(CustomProjectionMapper.class);

    private final AttachService attachService;
    private final ChannelService channelService;
    @Autowired
    @Lazy
    private VideoService videoService;

    public VideoShortInfoDTO toVideShortInfoDTO(VideoShortInfoProjection projection) {
        if (projection == null) {
            logger.warn("Received null projection in toVideShortInfoDTO");
            return null;
        }

        try {
            MediaUrlDTO mediaUrlDTO = new MediaUrlDTO(
                    attachService.getUrlOfMedia(projection.getPreviewAttachId())
            );

            VideoChannelDTO videoChannelDTO = new VideoChannelDTO(
                    channelService.getVideoChannelShortInfo(projection.getChannelId())
            );

            VideoShortInfoDTO videoShortInfoDTO = new VideoShortInfoDTO(
                    projection.getId(),
                    projection.getTitle(),
                    mediaUrlDTO,
                    videoChannelDTO,
                    projection.getViewCount(),
                    projection.getDuration(),
                    projection.getPublishedDate()
            );

            logger.debug("Successfully mapped VideoShortInfoDTO for video id: {}", projection.getId());
            return videoShortInfoDTO;
        } catch (Exception e) {
            logger.error("Error mapping VideoShortInfoDTO for projection: {}", projection, e);
            throw e;
        }
    }

    public VideoPlayListInfoDTO videoPlayListInfoDTODTO(VideoShortInfoProjection projection) {
        if (projection == null) {
            logger.warn("Received null projection in videoPlayListInfoDTODTO");
            return null;
        }

        try {
            MediaUrlDTO mediaUrlDTO = new MediaUrlDTO(
                    attachService.getUrlOfMedia(projection.getPreviewAttachId())
            );

            VideoPlayListInfoDTO videoPlayListInfoDTO = new VideoPlayListInfoDTO(
                    projection.getId(),
                    projection.getTitle(),
                    mediaUrlDTO,
                    projection.getViewCount(),
                    projection.getDuration(),
                    projection.getPublishedDate()
            );

            logger.debug("Successfully mapped VideoPlayListInfoDTO for video id: {}", projection.getId());
            return videoPlayListInfoDTO;
        } catch (Exception e) {
            logger.error("Error mapping VideoPlayListInfoDTO for projection: {}", projection, e);
            throw e;
        }
    }

    public AdminVideoInfoDTO toAdminVideoInfoDTO(AdminVideoProjection projection) {
        if (projection == null) {
            logger.warn("Received null projection in toAdminVideoInfoDTO");
            return null;
        }

        try {
            // Map VideoShortInfoDTO
            VideoShortInfoDTO videoShortInfoDTO = new VideoShortInfoDTO(
                    projection.getId(),
                    projection.getTitle(),
                    new MediaUrlDTO(attachService.getUrlOfMedia(projection.getPreviewAttachId())),
                    new VideoChannelDTO(channelService.getVideoChannelShortInfo(projection.getChannelId())),
                    projection.getViewCount(),
                    projection.getDuration(),
                    projection.getPublishedDate()
            );

            // Map OwnerInfoDTO
            OwnerInfoDTO ownerInfoDTO = new OwnerInfoDTO(
                    projection.getOwnerId(),
                    projection.getOwnerName(),
                    projection.getOwnerSurname()
            );

            // Map PlaylistInfoDTO
            PlaylistInfoDTO playlistInfoDTO = new PlaylistInfoDTO(
                    projection.getPlaylistId(),
                    projection.getPlaylistName()
            );

            // Combine all into AdminVideoInfoDTO
            AdminVideoInfoDTO adminVideoInfoDTO = new AdminVideoInfoDTO(videoShortInfoDTO, ownerInfoDTO, playlistInfoDTO);

            logger.debug("Successfully mapped AdminVideoInfoDTO for video id: {}", projection.getId());
            return adminVideoInfoDTO;
        } catch (Exception e) {
            logger.error("Error mapping AdminVideoInfoDTO for projection: {}", projection, e);
            throw e;
        }
    }

    public VideoShareDto toVideoShareDto(VideoShareProjection projection) {
        if (projection == null) {
            return null;
        }

        VideoShareDto dto = new VideoShareDto();
        dto.setVideoId(projection.getId());
        dto.setVideoTitle(projection.getTitle());
        dto.setChannelName(projection.getChannelName());
        dto.setSharedCount(projection.getSharedCount());

        // Construct URLs using your attachment service
        dto.setPreviewUrl(attachService.getUrlOfMedia(projection.getPreviewAttachId()));
        dto.setVideoUrl(videoService.generateVideoWatchUrl(projection.getId()));

        return dto;
    }

    public CommentOwnerInfo toCommentOwnerInfo(CommentOwnerInfoProjection projection) {

        CommentOwnerInfo info = new CommentOwnerInfo();
        info.setId(projection.getId());
        info.setName(projection.getName());
        info.setSurname(projection.getSurname());
        info.setPhoto(attachService.getUrlOfMedia(projection.getPhotoId()));

        return info;
    }
}