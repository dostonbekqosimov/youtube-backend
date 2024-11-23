package dasturlash.uz.service.auth;

import dasturlash.uz.dto.JwtDTO;
import dasturlash.uz.dto.JwtResponseDTO;
import dasturlash.uz.dto.TokenDTO;
import dasturlash.uz.dto.VerificationDTO;
import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.UnauthorizedException;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.security.CustomUserDetails;
import dasturlash.uz.service.ResourceBundleService;
import dasturlash.uz.util.JwtUtil;
import io.jsonwebtoken.JwtException;
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

    public String registrationConfirm(VerificationDTO dto, LanguageEnum lang) {
        Profile profile = profileRepository.findByEmailAndVisibleTrue(dto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("Profile not found"));

        return emailAuthService.confirmEmail(profile, dto.getCode(), lang);
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


        profileRepository.findByEmailAndVisibleTrue(email)
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

    public TokenDTO getNewAccessToken(TokenDTO dto, LanguageEnum lang) {
        // First check if refresh token is provided
        if (dto.getRefreshToken() == null || dto.getRefreshToken().trim().isEmpty()) {
            throw new AppBadRequestException(resourceBundleService.getMessage("refresh.token.required", lang));
        }

        // Validate the refresh token
        JwtUtil.TokenValidationResult validationResult = JwtUtil.validateToken(dto.getRefreshToken());
        if (!validationResult.isValid()) {
            throw new UnauthorizedException(validationResult.getMessage());
        }
        try {
            JwtDTO jwtDTO = JwtUtil.decode(dto.getRefreshToken());

            Profile profile = profileRepository.findByEmailAndVisibleTrue(jwtDTO.getLogin())
                    .orElseThrow(() -> new UnauthorizedException(resourceBundleService.getMessage("refresh.token.invalid", lang)));

            // Check if user is still active
            if (!profile.getStatus().equals(ProfileStatus.ACTIVE)) {
                throw new UnauthorizedException(resourceBundleService.getMessage("account.not.active", lang));
            }

            TokenDTO response = new TokenDTO();
            response.setAccessToken(JwtUtil.encode(profile.getEmail(), profile.getRole().name()));
            response.setRefreshToken(JwtUtil.refreshToken(profile.getEmail(), profile.getRole().name()));
            return response;

        } catch (JwtException e) {
            throw new UnauthorizedException(resourceBundleService.getMessage("refresh.token.invalid", lang));
        }
    }


}
