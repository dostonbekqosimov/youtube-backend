package dasturlash.uz.dto.request.video;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoCategoryDTO {
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
