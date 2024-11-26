package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VideoShortInfoDTO {

    private String id;
    private String title;
    private MediaUrlDTO previewAttach;
    private VideoChannelDTO channel;
    private Integer viewCount;
    private String duration;

    public VideoShortInfoDTO(String id, String title, MediaUrlDTO previewAttach, VideoChannelDTO channel, Integer viewCount, String duration) {
        this.id = id;
        this.title = title;
        this.previewAttach = previewAttach;
        this.channel = channel;
        this.viewCount = viewCount;
        this.duration = duration;
    }
}
