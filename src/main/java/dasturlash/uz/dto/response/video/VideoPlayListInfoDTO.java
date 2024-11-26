package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoPlayListInfoDTO {
    private String id;
    private String title;
    private MediaUrlDTO previewAttach;
    private Integer viewCount;
    private String duration;
    private LocalDateTime publishedDate;

    public VideoPlayListInfoDTO(String id, String title, MediaUrlDTO previewAttach, Integer viewCount, String duration,LocalDateTime publishedDate) {
        this.id = id;
        this.title = title;
        this.previewAttach = previewAttach;
        this.viewCount = viewCount;
        this.duration = duration;
        this.publishedDate = publishedDate;
    }
}
