package dasturlash.uz.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequestDTO {

//    @NotNull(message = "orderNumber cannot be null")
//    private Integer orderNumber;

    @NotBlank(message = "nameUz cannot be empty")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    private String name;

}
