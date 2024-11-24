package dasturlash.uz.dto.response.video;

import lombok.Data;

@Data
public class VideoLikeDTO {
    private Long likeCount;
    private Long dislikeCount;
    private Boolean isUserLiked;
    private Boolean isUserDisliked;
}