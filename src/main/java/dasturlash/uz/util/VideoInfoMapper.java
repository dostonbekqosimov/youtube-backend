package dasturlash.uz.util;


import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.OwnerInfoDTO;
import dasturlash.uz.dto.response.PlaylistInfoDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.video.AdminVideoInfoDTO;
import dasturlash.uz.dto.response.video.VideoPlayListInfoDTO;
import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
import dasturlash.uz.mapper.AdminVideoProjection;
import dasturlash.uz.mapper.VideoShortInfoProjection;
import dasturlash.uz.service.AttachService;
import dasturlash.uz.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoInfoMapper {

    private final AttachService attachService;
    private final ChannelService channelService;

    public VideoShortInfoDTO toVideShortInfoDTO(VideoShortInfoProjection projection) {
        if (projection == null) return null;

        MediaUrlDTO mediaUrlDTO = new MediaUrlDTO(
                attachService.getUrlOfMedia(projection.getPreviewAttachId())
        );

        VideoChannelDTO videoChannelDTO = new VideoChannelDTO(
                channelService.getVideoChannelShortInfo(projection.getChannelId())
        );

        return new VideoShortInfoDTO(
                projection.getId(),
                projection.getTitle(),
                mediaUrlDTO,
                videoChannelDTO,
                projection.getViewCount(),
                projection.getDuration(),
                projection.getPublishedDate()
        );
    }

    public VideoPlayListInfoDTO videoPlayListInfoDTODTO(VideoShortInfoProjection projection) {
        if (projection == null) return null;

        MediaUrlDTO mediaUrlDTO = new MediaUrlDTO(
                attachService.getUrlOfMedia(projection.getPreviewAttachId())
        );

        return new VideoPlayListInfoDTO(
                projection.getId(),
                projection.getTitle(),
                mediaUrlDTO,
                projection.getViewCount(),
                projection.getDuration(),
                projection.getPublishedDate()
        );
    }

    public AdminVideoInfoDTO toAdminVideoInfoDTO(AdminVideoProjection projection) {
        if (projection == null) return null;

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
        return new AdminVideoInfoDTO(videoShortInfoDTO, ownerInfoDTO, playlistInfoDTO);
    }
}
