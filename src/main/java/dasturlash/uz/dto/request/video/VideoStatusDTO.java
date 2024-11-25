package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoStatusDTO {
    @NotNull(message = "Status is required")
    private ContentStatus status;
    @Future
    private LocalDateTime scheduledDate;
}
