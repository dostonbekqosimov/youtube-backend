package dasturlash.uz.mapper;

import dasturlash.uz.enums.NotificationType;

public interface GetSubscriptionChannelsInfoMapper {
    String getSubsId();
    String getChannelId();
    String getChannelName();
    String getChannelPhoto();
    NotificationType getNotificationType();

}
