package dasturlash.uz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachDTO {

    private String id;
    private String originName;
    private Long size;
    private String path;
    private String type;
    private String duration;
    private LocalDateTime createdData;


    private String url;
    private String extension;


}
