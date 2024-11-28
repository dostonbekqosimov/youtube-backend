package dasturlash.uz.dto.response.playlist;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaylistBasicInfo {
    private String id;
    private String name;
    private Integer videoCount;
    private Integer totalViewCount;
    private LocalDateTime updatedDate;
}
