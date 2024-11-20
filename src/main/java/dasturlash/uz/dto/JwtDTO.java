package dasturlash.uz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {
    private String login;
    private String role;
}
