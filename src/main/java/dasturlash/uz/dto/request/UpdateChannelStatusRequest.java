package dasturlash.uz.dto.request;

import dasturlash.uz.enums.ChannelStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateChannelStatusRequest {
    @NotBlank
    private String channelId;
    private ChannelStatus status;


}

