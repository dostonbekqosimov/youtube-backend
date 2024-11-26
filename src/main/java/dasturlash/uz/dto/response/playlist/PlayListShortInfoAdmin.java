package dasturlash.uz.dto.response.playlist;

import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayListShortInfoAdmin {
    private String id;
    private String name;
    private String description;
    private ContentStatus status;
    private Integer orderNumber;
    private String channelId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean visible;
}
