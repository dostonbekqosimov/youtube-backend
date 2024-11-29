package dasturlash.uz.service.email;


import dasturlash.uz.dto.response.ResponseCustom;
import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;

import dasturlash.uz.enums.EmailStatus;
import dasturlash.uz.mapper.EmailHistoryInfoMapper;
import dasturlash.uz.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {
    private final EmailHistoryRepository emailHistoryRepository;


    public EmailHistory createEmailHistory(String toEmail, String subject, String message, Profile profile, Integer verificationCode) {
        EmailHistory history = new EmailHistory();
        history.setToAccount(toEmail);
        history.setSubject(subject);
        history.setMessage(message);
        history.setSentAt(LocalDateTime.now());
        history.setProfile(profile);
        history.setAttemptCount(1);
        history.setStatus(EmailStatus.PENDING);
        history.setEmail(toEmail);
        history.setVerificationCode(verificationCode);
        return emailHistoryRepository.save(history);
    }

    public void updateEmailStatus(EmailHistory history, EmailStatus status) {
        history.setStatus(status);
        if (status == EmailStatus.RESENT) {
            history.setAttemptCount(history.getAttemptCount() + 1);
        }
        emailHistoryRepository.save(history);
    }

    public ResponseCustom getHistoryByCode(String code){
        EmailHistoryInfoMapper emailAndTime = emailHistoryRepository.getEmailAndSentAtByCode(code);
        ResponseCustom responseCustom = new ResponseCustom();

        LocalDateTime now = LocalDateTime.now().minusMinutes(3);
        //check expire
        if (emailAndTime != null && emailAndTime.getTime().isAfter(now)) {


            responseCustom.setMessage(emailAndTime.getEmail());
            responseCustom.setSuccess(true);
            return responseCustom;
        }

        responseCustom.setMessage("Code is incorrect");
        responseCustom.setSuccess(false);
        return responseCustom;
    }

    public List<EmailHistory> getEmailHistoryByEmail(String email) {
        return emailHistoryRepository.findByEmail(email);
    }

    public List<EmailHistory> getEmailHistoryByDate(LocalDateTime date) {
        return null;
    }

    public Page<EmailHistory> getEmailHistoryWithPagination(Pageable pageable) {
        return emailHistoryRepository.findAll(pageable);
    }
}