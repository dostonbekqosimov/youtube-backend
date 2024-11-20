package dasturlash.uz.service;

import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.repository.ProfileRepository;
import jakarta.validation.Valid;
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




    public String registerAccount(@Valid RegistrationDTO dto, LanguageEnum lang) {

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

    public String resendConfirmationEmail(String id, LanguageEnum lang) {
        return emailAuthService.resendEmailConfirmation(id, lang);
    }
}
