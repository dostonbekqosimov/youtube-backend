package dasturlash.uz.dto.response.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaUrlDTO {
    private String id;
    private String url;
    private String duration;
}
