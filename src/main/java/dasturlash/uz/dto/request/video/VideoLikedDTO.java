package dasturlash.uz.dto.request.video;

import dasturlash.uz.enums.LikeType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VideoLikedDTO {
    private String videoId;
    @NotNull
    private LikeType type;


}
