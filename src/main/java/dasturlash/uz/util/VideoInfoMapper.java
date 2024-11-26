package dasturlash.uz.util;


import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.video.VideoPlayListInfoDTO;
import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
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
}
