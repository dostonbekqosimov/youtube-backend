package dasturlash.uz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateDTO {

    @NotBlank(message = "Content must not be blank")
    private String content;

    @NotNull(message = "Video ID must not be null")
    private String videoId;

    private String replyId;
}
