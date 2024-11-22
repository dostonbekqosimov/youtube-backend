package dasturlash.uz.dto.request;

import dasturlash.uz.enums.ChannelStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChannelCreateRequest {

    @NotBlank(message = "Channel name is required.")
    @Size(min = 3, max = 100, message = "Channel name must be between 3 and 100 characters.")
    private String name;

    @NotBlank(message = "Channel description is required.")
    private String description;

    @NotBlank(message = "Channel handle is required.")
    private String handle; // unique name for channel

}
