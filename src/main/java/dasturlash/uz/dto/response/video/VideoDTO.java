package dasturlash.uz.dto.response.video;

import dasturlash.uz.enums.ContentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoDTO {
    private String id;
    private String title;
    private String description;
    private String previewUrl;
    private String videoUrl;
    private Integer viewCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer sharedCount;
    private ContentStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime publishedDate;
}
