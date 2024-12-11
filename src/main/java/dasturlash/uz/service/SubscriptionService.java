package dasturlash.uz.service;

import dasturlash.uz.dto.request.subscription.SubscriptionAddDTO;
import dasturlash.uz.dto.request.subscription.SubscriptionChangeNotificationStatusDTO;
import dasturlash.uz.dto.request.subscription.SubscriptionChangeStatusDTO;
import dasturlash.uz.dto.response.channel.SubscriptionInfoChannel;
import dasturlash.uz.dto.response.subscription.SubscriptionInfo;
import dasturlash.uz.entity.Subscription;
import dasturlash.uz.enums.ChannelStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.mapper.GetSubscriptionChannelsInfoMapper;
import dasturlash.uz.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final ChannelService channelService;
    private final ResourceBundleService resourceBundleService;
    @Autowired
    AttachService attachService;


    private Long currentUserId(){
        return getCurrentUserId();
    }

    public String create(SubscriptionAddDTO dto, LanguageEnum lang) {
        //O'zini kanaliga obuna bolaolmasligi uchun check

        //Tizimdagi userni kanallarini id si
        List<String> channelOwnerIds = channelService.getChannelOwnerIds(currentUserId());

        if (checkSubscription((currentUserId()), dto.getChannelId())) {
            repository.deleteByProfileIdAndChannelId((currentUserId()), dto.getChannelId());
            return resourceBundleService.getMessage("subscription.unsubscription", lang);
        }

        for (String channelOwnerId : channelOwnerIds) {
            if (channelOwnerId.equals(dto.getChannelId())){
                return resourceBundleService.getMessage("subscription.wrong", lang);
            }
        }
        Subscription subscription = new Subscription();
        subscription.setChannelId(dto.getChannelId());
        subscription.setProfileId((currentUserId()));
        subscription.setStatus(ChannelStatus.ACTIVE);
        subscription.setCreatedDate(LocalDateTime.now());
        subscription.setNotificationType(dto.getNotificationType());
        repository.save(subscription);
        return resourceBundleService.getMessage("subscription.created", lang);
    }



    public boolean checkSubscription(Long profileId, String channelId) {
        return repository.findByProfileIdAndChannelId(profileId, channelId) != null;
    }

    public String changeStatus(SubscriptionChangeStatusDTO dto) {
        String status = dto.getStatus().toString();
        repository.updateStatus(status, (currentUserId()), dto.getChannelId());
        return "Changed status to " + status;
    }

    public String changeNotificationStatus(SubscriptionChangeNotificationStatusDTO dto) {
        String status = dto.getNotificationType().toString();
        repository.updateNotificationStatus(status, (currentUserId()), dto.getChannelId());
        return "Notification status to " + status;
    }

    public List<SubscriptionInfo> getSubscribedChannels() {
        List<GetSubscriptionChannelsInfoMapper> userSubscribedChannelsMapper = repository.getUserSubscribedChannels(currentUserId());
        return toSubscriptionInfoList(userSubscribedChannelsMapper);
    }

    //for Admin
    public List<SubscriptionInfo> getSubscribedChannelsAdmin(Long userId) {
        List<GetSubscriptionChannelsInfoMapper> userSubscribedChannelsMapper = repository.getUserSubscribedChannels(userId);
        return toSubscriptionInfoList(userSubscribedChannelsMapper);
    }

    public List<SubscriptionInfo> toSubscriptionInfoList(List<GetSubscriptionChannelsInfoMapper> userSubscribedChannelsMapper) {
            List<SubscriptionInfo> subscriptionInfoList = new ArrayList<>();


        for (GetSubscriptionChannelsInfoMapper channelsMapper : userSubscribedChannelsMapper) {
            SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
            SubscriptionInfoChannel channelInfo = new SubscriptionInfoChannel();

            //Subscription ozini malumotlarini set qilamiz
            subscriptionInfo.setId(channelsMapper.getSubsId());
            subscriptionInfo.setNotificationType(channelsMapper.getNotificationType());
            //Channel ni malumotlarini set qilamiz
            channelInfo.setId(channelsMapper.getChannelId());
            channelInfo.setName(channelsMapper.getChannelName());
            channelInfo.setPhotoUrl(attachService.openURL(channelsMapper.getChannelPhoto()));

            subscriptionInfo.setChannel(channelInfo);
            subscriptionInfoList.add(subscriptionInfo);
        }
        return subscriptionInfoList;
    }


}
