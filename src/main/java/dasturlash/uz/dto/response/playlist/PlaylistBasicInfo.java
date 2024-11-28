package dasturlash.uz.dto.response.playlist;

import lombok.Data;

@Data
public class PlaylistBasicInfo {
    private String id;
    private String name;
    private Integer video_count;
    private Integer total_view_count;
}
