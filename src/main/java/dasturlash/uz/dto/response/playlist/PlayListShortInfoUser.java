package dasturlash.uz.dto.response.playlist;

import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlayListShortInfoUser {
    private String name;
    private String description;
    private ContentStatus status;
    private LocalDateTime createdDate;
}
