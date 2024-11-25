package dasturlash.uz.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
}
