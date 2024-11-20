package dasturlash.uz.dto;

import dasturlash.uz.enums.ProfileRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private Long id;
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String email;
    @NotNull
    private ProfileRole role;
    @NotNull
    @Size(min = 6, max = 50)
    private String password;

}
