package dasturlash.uz.dto.response.channel;

import dasturlash.uz.dto.response.MediaUrlDTO;
import lombok.Data;

@Data
public class VideoChannelDTO {
    private String id;
    private String name;
    private MediaUrlDTO photoUrl;
}
