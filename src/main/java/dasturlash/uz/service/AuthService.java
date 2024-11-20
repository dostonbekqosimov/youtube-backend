package dasturlash.uz.service;

import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResourceBundleService resourceBundleService;
    private final EmailAuthService emailAuthService;


    public String registerAccount(RegistrationDTO dto, LanguageEnum lang) {

        Profile profile = new Profile();
        profile.setName(dto.getName());
        profile.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        profile.setSurname(dto.getSurname());
        profile.setCreatedDate(LocalDateTime.now());
        profile.setRole(ProfileRole.ROLE_USER);
        profile.setVisible(Boolean.TRUE);
        profile.setStatus(ProfileStatus.IN_REGISTERED);


        return emailAuthService.registerViaEmail(dto, profile, lang);

    }

    public String registrationConfirm(Long profileId, LanguageEnum lang) {
        return emailAuthService.confirmEmail(profileId, lang);
    }

    public String resendConfirmationEmail(Long profileId, LanguageEnum lang) {
        return emailAuthService.resendEmailConfirmation(profileId, lang);
    }

    private void existsByEmail(String email, LanguageEnum lang) {

        if (email != null && !email.trim().isEmpty()) {
            boolean isEmailExist = profileRepository.existsByEmailAndVisibleTrue(email);
            if (isEmailExist) {
                throw new DataExistsException(resourceBundleService.getMessage("email.exists", lang));
            }
        }
    }
}
