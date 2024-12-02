package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import lombok.Data;

@Data
public class VideoHistoryDTO {
    private String title;
    private String channelName;
    private String viewCount;
    private String duration;
    private String url;
}
