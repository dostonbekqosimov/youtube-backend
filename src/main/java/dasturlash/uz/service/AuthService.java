package dasturlash.uz.service;

import dasturlash.uz.dto.JwtResponseDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.exceptions.UnauthorizedException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.CustomUserDetails;
import dasturlash.uz.util.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResourceBundleService resourceBundleService;
    private final EmailAuthService emailAuthService;


    public String registerAccount(RegistrationDTO dto, LanguageEnum lang) {

        // check if the email already exists in database
        existsByEmail(dto.getEmail(), lang);

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
        System.out.println("profileId: " + profileId);
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

    public JwtResponseDTO login(String email, String password, LanguageEnum lang) {


        Profile entity = profileRepository.findByEmailAndVisibleTrue(email)
                .orElseThrow(() -> new UnauthorizedException(resourceBundleService.getMessage("login.password.wrong", lang)));


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication.isAuthenticated()) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                JwtResponseDTO response = new JwtResponseDTO();
                response.setEmail(email);
                response.setToken(JwtUtil.encode(email, userDetails.getRole().toString()));
                response.setRefreshToken(JwtUtil.refreshToken(email, userDetails.getRole().toString()));
                response.setRoles(List.of(userDetails.getRole().toString()));

//                ProfileResponseDTO responseDTO = new ProfileResponseDTO();
//                responseDTO.setName(entity.getName());
//                responseDTO.setSurname(entity.getSurname());
//                responseDTO.setLogin(entity.getLogin());
//                responseDTO.setRole(entity.getRole());
//                responseDTO.setAccessToken(JwtUtil.encode(login, userDetails.getRole().toString()));
//                responseDTO.setRefreshToken(JwtUtil.refreshToken(login, userDetails.getRole().toString()));
//                responseDTO.setPhoto(attachService.getDto(entity.getPhotoId()));


                return response;
            }
            throw new UnauthorizedException(resourceBundleService.getMessage("login.password.wrong", lang));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(resourceBundleService.getMessage("login.password.wrong", lang));

        }
    }
}
