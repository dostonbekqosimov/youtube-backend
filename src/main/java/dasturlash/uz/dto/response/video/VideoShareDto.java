package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoShareDto {

    private String videoId;
    private String videoTitle;
    private String  videoUrl;
    private MediaUrlDTO previewUrl;

    private Integer sharedCount; // Optional: Current shared count
    private String channelName; // Optional: Channel or creator name
}
