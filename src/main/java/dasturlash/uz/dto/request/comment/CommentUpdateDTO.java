package dasturlash.uz.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentUpdateDTO {
    @NotNull(message = "Comment ID must not be null")
    private String id;

    @NotBlank(message = "Content must not be blank")
    private String content;
}
