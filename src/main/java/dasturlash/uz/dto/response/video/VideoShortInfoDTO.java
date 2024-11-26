package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class VideoShortInfoDTO {

    private String id;
    private String title;
    private MediaUrlDTO previewAttach;
    private VideoChannelDTO channel;
    private Integer viewCount;
    private String duration;
    private LocalDateTime publishedDate;

    public VideoShortInfoDTO(String id, String title, MediaUrlDTO previewAttach, VideoChannelDTO channel, Integer viewCount, String duration, LocalDateTime publishedDate) {
        this.id = id;
        this.title = title;
        this.previewAttach = previewAttach;
        this.channel = channel;
        this.viewCount = viewCount;
        this.duration = duration;
        this.publishedDate = publishedDate;
    }
}
