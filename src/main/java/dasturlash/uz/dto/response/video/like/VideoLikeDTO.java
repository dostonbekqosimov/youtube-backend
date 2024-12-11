package dasturlash.uz.dto.response.video.like;

import lombok.Data;

@Data
public class VideoLikeDTO {
    private Integer likeCount;
    private Integer dislikeCount;
    private Boolean isUserLiked;
    private Boolean isUserDisliked;
}