package dasturlash.uz.controller;

import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.AuthService;
import dasturlash.uz.util.LanguageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(
            @RequestBody @Valid RegistrationDTO dto,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.registerAccount(dto, lang));
    }

    @GetMapping("/registration/confirm/{profileId}")
    public ResponseEntity<String> registrationConfirm(
            @PathVariable Long profileId,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.registrationConfirm(profileId, lang));
    }

    @PostMapping("/registration/resend/{profileId}")
    public ResponseEntity<String> resendConfirmation(
            @PathVariable Long profileId,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.resendConfirmationEmail(profileId, lang));
    }
}
