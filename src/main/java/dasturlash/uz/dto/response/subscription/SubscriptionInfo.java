package dasturlash.uz.dto.response.subscription;

import dasturlash.uz.dto.response.channel.SubscriptionInfoChannel;
import dasturlash.uz.enums.NotificationType;
import lombok.Data;

@Data
public class SubscriptionInfo {
    private String id;
    private SubscriptionInfoChannel channel;
    private NotificationType notificationType;
}
