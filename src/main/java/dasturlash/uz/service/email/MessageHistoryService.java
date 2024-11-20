package dasturlash.uz.service.email;


import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;

import dasturlash.uz.enums.EmailStatus;
import dasturlash.uz.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageHistoryService {
    private final EmailHistoryRepository emailHistoryRepository;


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


}