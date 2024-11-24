package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoPlaylistDTO {
    private String playlistId;
}
