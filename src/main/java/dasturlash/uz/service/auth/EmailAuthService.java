package dasturlash.uz.service.auth;


import dasturlash.uz.dto.MessageDTO;
import dasturlash.uz.dto.request.RegistrationDTO;
import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.EmailStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.repository.EmailHistoryRepository;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.service.ResourceBundleService;
import dasturlash.uz.service.email.EmailSendingService;
import dasturlash.uz.service.email.EmailTemplateService;
import dasturlash.uz.service.email.EmailHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static dasturlash.uz.util.RandomUtil.getRandomInt;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final ProfileRepository profileRepository;
    private final EmailHistoryRepository emailHistoryRepository;
    private final EmailSendingService emailSendingService;
    private final EmailTemplateService emailTemplateService;
    private final EmailHistoryService emailHistoryService;
    private final ResourceBundleService resourceBundleService;


    @Value("${registration.confirmation.deadline.minutes}")
    private int confirmationDeadlineMinutes;

    @Value("${registration.max.resend.attempts}")
    private int maxResendAttempts;

    public String registerViaEmail(RegistrationDTO dto, Profile profile, LanguageEnum lang) {


        profile.setEmail(dto.getEmail());
        profileRepository.save(profile);

        Integer verificationCode = getRandomInt();

        String emailContent = emailTemplateService.getRegistrationEmailTemplate(
                profile.getId(),
                profile.getName(),
                confirmationDeadlineMinutes,
                maxResendAttempts,
                verificationCode
        );

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToAccount(dto.getEmail());
        messageDTO.setSubject(resourceBundleService.getMessage("email.set.subject", lang));
        messageDTO.setText(emailContent);

        emailSendingService.sendMimeMessage(messageDTO, profile, verificationCode);
        // Note: EmailSendingService will handle creating and updating the history

        return resourceBundleService.getMessage("email.confirmation.sent", lang);
    }

    public String confirmEmail(Profile profile, Integer code, LanguageEnum lang) {


        EmailHistory emailHistory = emailHistoryRepository.findTopByEmailOrderBySentAtDesc(profile.getEmail())
                .orElseThrow(() -> new DataNotFoundException("No email history found"));

        // Check if code matches
        if (!emailHistory.getVerificationCode().equals(code)) {
            emailHistoryService.updateEmailStatus(emailHistory, EmailStatus.FAILED);
            throw new AppBadRequestException(resourceBundleService.getMessage("email.invalid.verification.code", lang));
        }

        // Check expiration
        LocalDateTime exp = LocalDateTime.now().minusMinutes(confirmationDeadlineMinutes);
        if (exp.isAfter(emailHistory.getSentAt())) {
            profile.setStatus(ProfileStatus.IN_REGISTERED);
            emailHistoryService.updateEmailStatus(emailHistory, EmailStatus.EXPIRED);
            profileRepository.save(profile);
            return resourceBundleService.getMessage("email.confirmation.expired", lang);
        }

        if (!profile.getStatus().equals(ProfileStatus.IN_REGISTERED)) {
            emailHistoryService.updateEmailStatus(emailHistory, EmailStatus.FAILED);
            return resourceBundleService.getMessage("email.registration.not.completed", lang);
        }

        profile.setStatus(ProfileStatus.ACTIVE);
        emailHistoryService.updateEmailStatus(emailHistory, EmailStatus.DELIVERED);
        profileRepository.save(profile);
        return resourceBundleService.getMessage("email.registration.completed", lang);
    }

    public String resendEmailConfirmation(Long id, LanguageEnum lang) {
        Profile profile = profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Profile not found"));

        EmailHistory lastHistory = emailHistoryRepository.findTopByEmailOrderBySentAtDesc(profile.getEmail())
                .orElseThrow(() -> new DataNotFoundException("No email history found"));

        if (lastHistory.getAttemptCount() >= maxResendAttempts) {
            emailHistoryService.updateEmailStatus(lastHistory, EmailStatus.FAILED);
            profile.setStatus(ProfileStatus.BLOCKED);
            profileRepository.save(profile);
            throw new DataNotFoundException(resourceBundleService.getMessage("email.max.resend.attempts.exceeded", lang));
        }

        if (profile.getStatus().equals(ProfileStatus.ACTIVE)) {
            emailHistoryService.updateEmailStatus(lastHistory, EmailStatus.FAILED);
            return "Profile is already active";
        }

        Integer verificationCode = getRandomInt();

        String emailContent = emailTemplateService.getResendConfirmationEmailTemplate(
                profile.getId(),
                profile.getName(),
                confirmationDeadlineMinutes,
                maxResendAttempts - lastHistory.getAttemptCount(),
                verificationCode
        );

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToAccount(profile.getEmail());
        messageDTO.setSubject("Registration Confirmation - New Link");
        messageDTO.setText(emailContent);

        emailSendingService.sendMimeMessage(messageDTO, profile, verificationCode);
        // Note: EmailSendingService will handle creating and updating the history

        return resourceBundleService.getMessage("email.confirmation.resent", lang);
    }
}