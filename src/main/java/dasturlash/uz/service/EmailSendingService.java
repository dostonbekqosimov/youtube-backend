package dasturlash.uz.service;

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
    private final MessageHistoryService messageHistoryService;

    public String sendMimeMessage(MessageDTO dto, Profile profile) {

        EmailHistory history = messageHistoryService.createEmailHistory(
                dto.getToAccount(),
                dto.getSubject(),
                dto.getText(),
                profile
        );


        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            helper.setTo(dto.getToAccount());
            helper.setSubject(dto.getSubject());
            helper.setText(dto.getText(), true);
            javaMailSender.send(msg);

            // Update status to SENT after successful send
            messageHistoryService.updateEmailStatus(history, EmailStatus.SENT);


            return "Mail was sent successfully";
        } catch (MessagingException e) {
            messageHistoryService.updateEmailStatus(history, EmailStatus.FAILED);

            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

}
