package dasturlash.uz.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "Surname is required.")
    @Size(max = 100, message = "Surname cannot exceed 100 characters.")
    private String surname;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;
}