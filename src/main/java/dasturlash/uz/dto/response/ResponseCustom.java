package dasturlash.uz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseCustom {
    private int code;
    private String message;
    private Boolean success;

    public ResponseCustom(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public ResponseCustom() {
    }
}
