package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VideoPlaylistDTO {
    private List<String> newPlaylistIds;
    private List<String> removePlaylistIds;
}
