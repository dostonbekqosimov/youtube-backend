package dasturlash.uz.dto.response.video.like;

import lombok.Data;

@Data
public class LikedVideoInfo {
    private String videoId;
    private String name;
    private LikedVideoChannelInfo channel;
    private String duration;

}
