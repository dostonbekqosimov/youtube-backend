package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.channel.MediaUrlDTO;
import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoFullInfoDTO {
    private String id;
    private String title;
    private String description;
    private MediaUrlDTO previewAttach;
    private MediaUrlDTO videoAttach;
    private Integer viewCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer sharedCount;
    private ContentStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime publishedDate;
    private LocalDateTime scheduledDate;
}
