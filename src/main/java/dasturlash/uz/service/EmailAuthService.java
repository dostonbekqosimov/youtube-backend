package dasturlash.uz.service;


import dasturlash.uz.dto.MessageDTO;
import dasturlash.uz.dto.RegistrationDTO;
import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.EmailStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileStatus;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.repository.EmailHistoryRepository;
import dasturlash.uz.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final ProfileRepository profileRepository;
    private final EmailHistoryRepository emailHistoryRepository;
    private final EmailSendingService emailSendingService;
    private final EmailTemplateService emailTemplateService;
    private final MessageHistoryService messageHistoryService;
    private final ResourceBundleService resourceBundleService;


    @Value("${registration.confirmation.deadline.minutes}")
    private int confirmationDeadlineMinutes;

    @Value("${registration.max.resend.attempts}")
    private int maxResendAttempts;

    public String registerViaEmail(RegistrationDTO dto, Profile profile, LanguageEnum lang) {

        profile.setEmail(dto.getLogin());
        profileRepository.save(profile);

        String emailContent = emailTemplateService.getRegistrationEmailTemplate(
                profile.getId(),
                profile.getName(),
                confirmationDeadlineMinutes,
                maxResendAttempts
        );

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToAccount(dto.getLogin());
        messageDTO.setSubject(resourceBundleService.getMessage("email.set.subject", lang));
        messageDTO.setText(emailContent);

        emailSendingService.sendMimeMessage(messageDTO, profile);
        // Note: EmailSendingService will handle creating and updating the history

        return resourceBundleService.getMessage("email.confirmation.sent", lang);
    }

    public String confirmEmail(String id, LanguageEnum lang) {
        Profile profile = profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Profile not found"));

        EmailHistory emailHistory = emailHistoryRepository.findTopByEmailOrderByCreatedDateDesc(profile.getEmail())
                .orElseThrow(() -> new DataNotFoundException("No email history found"));

        LocalDateTime exp = LocalDateTime.now().minusMinutes(confirmationDeadlineMinutes);
        if (exp.isAfter(emailHistory.getCreatedDate())) {
            profile.setStatus(ProfileStatus.IN_REGISTERED);
            messageHistoryService.updateEmailStatus(emailHistory, EmailStatus.EXPIRED);
            profileRepository.save(profile);
            return resourceBundleService.getMessage("email.confirmation.expired", lang);
        }

        if (!profile.getStatus().equals(ProfileStatus.IN_REGISTERED)) {
            messageHistoryService.updateEmailStatus(emailHistory, EmailStatus.FAILED);
            return "Not Completed";
        }

        profile.setStatus(ProfileStatus.ACTIVE);
        messageHistoryService.updateEmailStatus(emailHistory, EmailStatus.DELIVERED);
        profileRepository.save(profile);
        return "Completed";
    }

    public String resendEmailConfirmation(String id, LanguageEnum lang) {
        Profile profile = profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Profile not found"));

        EmailHistory lastHistory = emailHistoryRepository.findTopByEmailOrderByCreatedDateDesc(profile.getEmail())
                .orElseThrow(() -> new DataNotFoundException("No email history found"));

        if (lastHistory.getAttemptCount() >= maxResendAttempts) {
            messageHistoryService.updateEmailStatus(lastHistory, EmailStatus.FAILED);
            profile.setStatus(ProfileStatus.BLOCKED);
            profileRepository.save(profile);
            throw new DataNotFoundException(resourceBundleService.getMessage("email.max.resend.attempts.exceeded", lang));
        }

        if (profile.getStatus().equals(ProfileStatus.ACTIVE)) {
            messageHistoryService.updateEmailStatus(lastHistory, EmailStatus.FAILED);
            return "Profile is already active";
        }

        String emailContent = emailTemplateService.getRegistrationEmailTemplate(
                profile.getId(),
                profile.getName(),
                confirmationDeadlineMinutes,
                maxResendAttempts - lastHistory.getAttemptCount()
        );

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setToAccount(profile.getEmail());
        messageDTO.setSubject("Registration Confirmation - New Link");
        messageDTO.setText(emailContent);

        emailSendingService.sendMimeMessage(messageDTO, profile);
        // Note: EmailSendingService will handle creating and updating the history

        return resourceBundleService.getMessage("email.confirmation.resent", lang);
    }
}