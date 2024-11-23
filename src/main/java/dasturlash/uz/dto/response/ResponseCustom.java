package dasturlash.uz.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCustom {
    private int code;
    private String message;
    private Boolean success;
}
