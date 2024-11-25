package dasturlash.uz.dto.response.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoCreateResponseDTO {
    private String id;
    private String title;
    private String videoLink;    // YouTube-like link
    private boolean isPublic;    // To control showing share options
    private String message;      // Status-specific message
    private LocalDateTime scheduledDate;  // For scheduled videos
    private LocalDateTime publishedDate;  // For public videos
    private List<String> allowedSharePlatforms;  // Available sharing platforms

    // Add getters, setters or use @Data
}
