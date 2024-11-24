package dasturlash.uz.dto.request.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoPreviewDTO {
    @NotBlank(message = "Preview attachment ID is required")
    private String previewAttachId;
}
