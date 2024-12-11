package dasturlash.uz.dto.request.subscription;

import dasturlash.uz.enums.ChannelStatus;
import lombok.Data;

@Data
public class SubscriptionChangeStatusDTO {
    private String channelId;
    private ChannelStatus status;
}
