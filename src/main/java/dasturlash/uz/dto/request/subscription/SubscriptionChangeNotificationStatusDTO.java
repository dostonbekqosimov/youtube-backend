package dasturlash.uz.dto.request.subscription;

import dasturlash.uz.enums.NotificationType;
import lombok.Data;

@Data
public class SubscriptionChangeNotificationStatusDTO {
    private String channelId;
    private NotificationType notificationType;
}
