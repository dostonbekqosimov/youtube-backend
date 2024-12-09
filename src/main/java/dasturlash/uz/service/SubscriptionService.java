package dasturlash.uz.service;

import dasturlash.uz.dto.SubscriptionAddDTO;
import dasturlash.uz.entity.Subscription;
import dasturlash.uz.enums.ChannelStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final ChannelService channelService;
    private final ResourceBundleService resourceBundleService;


    public String create(SubscriptionAddDTO dto, LanguageEnum lang) {
        //O'zini kanaliga obuna bolaolmasligi uchun check
        Long currentUserId = getCurrentUserId();
        //Tizimdagi userni kanallarini id si
        List<String> channelOwnerIds = channelService.getChannelOwnerIds(currentUserId);

        for (String channelOwnerId : channelOwnerIds) {
            if (channelOwnerId.equals(dto.getChannelId())){
                resourceBundleService.getMessage("subscription.wrong", lang);
            }
        }
        Subscription subscription = new Subscription();
        subscription.setChannelId(dto.getChannelId());
        subscription.setProfileId(currentUserId);
        subscription.setStatus(ChannelStatus.ACTIVE);
        subscription.setCreatedDate(LocalDateTime.now());
        subscription.setNotificationType(dto.getNotificationType());
        repository.save(subscription);
        return resourceBundleService.getMessage("subscription.created", lang);
    }
}
