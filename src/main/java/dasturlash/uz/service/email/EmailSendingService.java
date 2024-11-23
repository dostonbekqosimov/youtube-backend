package dasturlash.uz.service.email;

import dasturlash.uz.dto.MessageDTO;

import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.EmailStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String fromAccount;

    private final JavaMailSender javaMailSender;
    private final EmailHistoryService emailHistoryService;

    public String sendMimeMessage(MessageDTO messageDTO, Profile profile, Integer verificationCode) {

        EmailHistory history = emailHistoryService.createEmailHistory(
                messageDTO.getToAccount(),
                messageDTO.getSubject(),
                messageDTO.getText(),
                profile,
                verificationCode
        );


        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(messageDTO.getToAccount());
            helper.setSubject(messageDTO.getSubject());
            helper.setText(messageDTO.getText(), true);
            javaMailSender.send(msg);

            // Update status to SENT after successful send
            emailHistoryService.updateEmailStatus(history, EmailStatus.SENT);


            return "Mail was sent successfully";
        } catch (MessagingException e) {
            emailHistoryService.updateEmailStatus(history, EmailStatus.FAILED);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

}
