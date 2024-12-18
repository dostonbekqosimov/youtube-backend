package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.VideoType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoUpdateDTO {


    @Size(max = 100, message = "Your title is too long")
    private String title;

    @Size(max = 5000, message = "Your description is too long")
    private String description;

    private Long categoryId;
    private List<String> newPlaylistIds;
    private List<String> removePlaylistIds;
    private String previewAttachId;
    private VideoType type;
    private ContentStatus status;
    private List<String> tags;

    @Future
    private LocalDateTime scheduledDate;
}


