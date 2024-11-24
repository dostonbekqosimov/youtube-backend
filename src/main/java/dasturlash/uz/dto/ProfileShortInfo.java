package dasturlash.uz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileShortInfo {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String photoUrl;
}
