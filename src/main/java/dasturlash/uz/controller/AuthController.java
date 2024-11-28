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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API endpoints for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user account",
            description = "Creates a new user account and initiates email verification process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User registration initiated successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400",
                    description = "Bad request (e.g., email already exists, validation error)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @PostMapping("/registration")
    public ResponseEntity<String> registration(
            @Parameter(description = "Registration details",
                    required = true,
                    schema = @Schema(implementation = RegistrationDTO.class))
            @RequestBody @Valid RegistrationDTO dto,
            @Parameter(description = "Language header for localized response",
                    example = "uz, en, ru")
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.registerAccount(dto, lang));
    }

    @Operation(summary = "Confirm user registration",
            description = "Verifies the email confirmation code sent during registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Email verified successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400",
                    description = "Invalid verification code or email",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @PostMapping("/registration/confirm")
    public ResponseEntity<String> registrationConfirm(
            @Parameter(description = "Verification details",
                    required = true,
                    schema = @Schema(implementation = VerificationDTO.class))
            @RequestBody @Valid VerificationDTO verificationDTO,
            @Parameter(description = "Language header for localized response",
                    example = "uz, en, ru")
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.registrationConfirm(verificationDTO, lang));
    }

    @Operation(summary = "Resend verification code",
            description = "Resends email verification code to the specified email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Verification code resent successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400",
                    description = "Invalid email or account already active",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @GetMapping("/registration/resend")
    public ResponseEntity<String> resendVerificationCode(
            @Parameter(description = "Email address to resend verification code",
                    required = true,
                    example = "user@example.com")
            @RequestParam("email") @Email String email,
            @Parameter(description = "Language header for localized response",
                    example = "uz, en, ru")
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.resendConfirmationEmail(email, lang));
    }

    @Operation(summary = "User login",
            description = "Authenticate user and generate JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponseDTO.class))),
            @ApiResponse(responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(
            @Parameter(description = "Login credentials",
                    required = true,
                    schema = @Schema(implementation = LoginDTO.class))
            @RequestBody @Valid LoginDTO loginDTO,
            @Parameter(description = "Language header for localized response",
                    example = "uz, en, ru")
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader
    ) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        return ResponseEntity.ok(authService.login(loginDTO.getEmail(), loginDTO.getPassword(), lang));
    }

    @Operation(summary = "Refresh access token",
            description = "Generate a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "New access token generated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid or missing refresh token",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - invalid or expired refresh token",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refreshToken(
            @Parameter(description = "Refresh token request",
                    required = true,
                    schema = @Schema(implementation = TokenRefreshRequestDTO.class))
            @Valid @RequestBody TokenRefreshRequestDTO request,
            @Parameter(description = "Language header for localized response",
                    example = "uz, en, ru")
            @RequestHeader(value = "Accept-Language", defaultValue = "uz") LanguageEnum lang
    ) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authService.getNewAccessToken(tokenDTO, lang));
    }
}
