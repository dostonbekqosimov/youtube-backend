package dasturlash.uz.controller;

import dasturlash.uz.dto.request.subscription.SubscriptionAddDTO;
import dasturlash.uz.dto.request.subscription.SubscriptionChangeNotificationStatusDTO;
import dasturlash.uz.dto.request.subscription.SubscriptionChangeStatusDTO;
import dasturlash.uz.dto.response.subscription.SubscriptionInfo;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.SubscriptionService;
import dasturlash.uz.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController{
    private final SubscriptionService service;

    @PostMapping("/create")
    public ResponseEntity<String> createSubscription(@RequestBody SubscriptionAddDTO dto,
                                                     @RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        LanguageEnum languageEnum = LanguageUtil.getLanguageFromHeader(lang);
        return ResponseEntity.ok(service.create(dto, languageEnum));
    }

    @PutMapping("/change-status")
    public ResponseEntity<String> changeStatus(@RequestBody SubscriptionChangeStatusDTO dto,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        LanguageEnum languageEnum = LanguageUtil.getLanguageFromHeader(lang);
        return ResponseEntity.ok(service.changeStatus(dto));
    }

    @PutMapping("/change-notification-status")
    public ResponseEntity<String> changeNotificationStatus(@RequestBody SubscriptionChangeNotificationStatusDTO dto,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        LanguageEnum languageEnum = LanguageUtil.getLanguageFromHeader(lang);
        return ResponseEntity.ok(service.changeNotificationStatus(dto));
    }

    @GetMapping("/get-subscribed-channels")
    public ResponseEntity<List<SubscriptionInfo>> getSubscribedChannels(@RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        return ResponseEntity.ok(service.getSubscribedChannels());
    }

    @GetMapping("/get-subscribed-channels-Admin/{userId}")
    public ResponseEntity<List<SubscriptionInfo>> getSubscribedChannelsAdmin(@PathVariable Long userId,
                                                                             @RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        return ResponseEntity.ok(service.getSubscribedChannelsAdmin(userId));
    }


}
