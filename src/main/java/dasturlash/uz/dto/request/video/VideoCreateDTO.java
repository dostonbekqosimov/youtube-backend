package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.VideoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoCreateDTO {
    @NotBlank(message = "Your video needs a title")
    @Size(max = 100, message = "Your title is too long")
    private String title;
    @Size(max = 5000, message = "Your description is too long")
    private String description;
    @NotNull
    private Long categoryId;

    // playlist can be null
    private String playlistId;

    @NotBlank
    private String attachId;
    @NotBlank
    private String previewAttachId;
    @NotNull
    private VideoType type;

    @NotBlank(message = "Channel ID is required")
    @NotNull
    private String channelId;

    @NotNull(message = "Status is required")
    private ContentStatus status;

    private LocalDateTime publishedDate;
}
