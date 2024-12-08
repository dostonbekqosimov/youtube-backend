package dasturlash.uz.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
import dasturlash.uz.entity.Profile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentInfoDTO {
    private String id;

    private String content;

    private VideoShortInfoDTO videoDetails;

    private CommentOwnerInfo profile;

    private Integer likeCount;

    private Integer dislikeCount;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}
