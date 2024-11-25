package dasturlash.uz.dto.request.playlist;

import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

@Data
public class PlaylistDTO {
    private String channelId;
    private String name;
    private String description;
    private ContentStatus status;
    private Integer orderNumber;


}
