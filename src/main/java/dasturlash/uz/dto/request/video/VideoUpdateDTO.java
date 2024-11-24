package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.VideoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoUpdateDTO {


    @Size(max = 100, message = "Your title is too long")
    private String title;

    @Size(max = 5000, message = "Your description is too long")
    private String description;

    private Long categoryId;
    private String playlistId;
    private String previewAttachId;
    private VideoType type;
    private ContentStatus status;

    private LocalDateTime updatedDate;
    private LocalDateTime scheduledDate;
}


