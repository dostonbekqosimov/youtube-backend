package dasturlash.uz.dto.response.video.like;

import lombok.Data;

@Data
public class VideoLikeInfo {
    private String videoLikeId;
    private LikedVideoInfo video;
    private LikedAttachInfo preview;
}
