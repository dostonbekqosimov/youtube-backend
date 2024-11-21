package dasturlash.uz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDetailDTO {
    @NotNull
    private String name;
    @NotNull
    private String surname;
}
