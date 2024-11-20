package dasturlash.uz.service;



import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.entity.SmsHistory;
import dasturlash.uz.enums.EmailStatus;
import dasturlash.uz.enums.SmsStatus;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.repository.EmailHistoryRepository;
import dasturlash.uz.repository.SmsHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {
    private final EmailHistoryRepository emailHistoryRepository;
    private final SmsHistoryRepository smsHistoryRepository;

    public EmailHistory createEmailHistory(String toEmail, String subject, String message, Profile profile) {
        EmailHistory history = new EmailHistory();
        history.setToAccount(toEmail);
        history.setSubject(subject);
        history.setMessage(message);
        history.setSentAt(LocalDateTime.now());
        history.setProfile(profile);
        history.setCreatedDate(LocalDateTime.now());
        history.setAttemptCount(1);
        history.setStatus(EmailStatus.PENDING);
        history.setEmail(toEmail);
        return emailHistoryRepository.save(history);
    }

    public void updateEmailStatus(EmailHistory history, EmailStatus status) {
        history.setStatus(status);
        if (status == EmailStatus.RESENT) {
            history.setAttemptCount(history.getAttemptCount() + 1);
        }
        emailHistoryRepository.save(history);
    }

    public SmsHistory createSmsHistory(String phone, String verificationCode) {
        SmsHistory history = new SmsHistory();
        history.setPhone(phone);
        history.setVerificationCode(verificationCode);
        history.setCreatedDate(LocalDateTime.now());
        history.setAttemptCount(1);
        history.setStatus(SmsStatus.PENDING_CONFIRMATION);
        return smsHistoryRepository.save(history);
    }

    public void updateSmsStatus(SmsHistory history, SmsStatus status) {
        history.setStatus(status);
        if (status == SmsStatus.RESENT) {
            history.setAttemptCount(history.getAttemptCount() + 1);
        }
        smsHistoryRepository.save(history);
    }

    public SmsHistory findLatestSmsHistory(String phoneNumber) {
        return smsHistoryRepository.findTopByPhoneOrderByCreatedDateDesc(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("No SMS history found for phone number: " + phoneNumber));
    }
}