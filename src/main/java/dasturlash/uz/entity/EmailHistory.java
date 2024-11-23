package dasturlash.uz.entity;



import dasturlash.uz.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_history")
public class EmailHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String toAccount;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", length = 65535)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Enumerated(value = EnumType.STRING)
    private EmailStatus status;

    private Integer attemptCount = 0;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String email;

    private Integer verificationCode;

    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false,insertable = false)
    private Profile profile;
}
