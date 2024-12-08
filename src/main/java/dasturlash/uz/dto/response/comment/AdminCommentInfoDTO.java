package dasturlash.uz.dto.response.comment;

import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCommentInfoDTO {

    private String id;

    private String content;

    private VideoShortInfoDTO videoDetails;

    private Integer likeCount;

    private Integer dislikeCount;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

 


}
