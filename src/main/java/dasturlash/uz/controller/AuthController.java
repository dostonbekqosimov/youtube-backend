package dasturlash.uz.controller;

import dasturlash.uz.dto.JwtResponseDTO;
import dasturlash.uz.dto.TokenDTO;
import dasturlash.uz.dto.VerificationDTO;
import dasturlash.uz.dto.request.TokenRefreshRequestDTO;
import dasturlash.uz.dto.request.LoginDTO;
import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.auth.AuthService;
import dasturlash.uz.util.LanguageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/registration/confirm")
    public ResponseEntity<String> registrationConfirm(
            @RequestBody @Valid VerificationDTO verificationDTO,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.registrationConfirm(verificationDTO, lang));
    }

    @PostMapping("/registration/resend/{profileId}")
    public ResponseEntity<String> resendConfirmation(
            @PathVariable Long profileId,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.resendConfirmationEmail(profileId, lang));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO,
                                                @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader) {

        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.login(loginDTO.getEmail(), loginDTO.getPassword(), lang));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO request,
                                                 @RequestHeader(value = "Accept-Language", defaultValue = "uz") LanguageEnum lang) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authService.getNewAccessToken(tokenDTO, lang));
    }
}

/*
   // Endpoint for resending verification code
    @PostMapping("/verify/resend")
    public ResponseEntity<String> resendVerificationCode(
            @RequestParam @Email String email,
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.resendVerificationCode(email, lang));
    }
* */
