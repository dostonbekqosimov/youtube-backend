package dasturlash.uz.dto.response;

import lombok.Data;

@Data
public class VideoPlaylistDTO {
    private String id;
    private String title;
    private Integer totalVideoCount;
    private Integer orderNumber;
}
