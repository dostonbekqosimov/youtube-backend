package dasturlash.uz.dto.request.playlist;

import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

@Data
public class ChangeStatusDTO {
    private String playlistId;
    private ContentStatus status;
}
