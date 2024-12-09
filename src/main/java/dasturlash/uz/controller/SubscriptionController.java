package dasturlash.uz.controller;

import dasturlash.uz.dto.SubscriptionAddDTO;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.SubscriptionService;
import dasturlash.uz.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
