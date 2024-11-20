package dasturlash.uz.repository;


import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.enums.EmailStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, String> {
    List<EmailHistory> findByProfileIdOrderBySentAtDesc(Long profileId);
    List<EmailHistory> findByToAccountOrderBySentAtDesc(String email);

    List<EmailHistory> findByStatus(EmailStatus status);

//    List<EmailHistory> findByCreatedDate(LocalDateTime date);

    List<EmailHistory> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update EmailHistory set attemptCount = attemptCount + 1 where id = ?1")
    void increaseAttemptCount(String id);

    Optional<EmailHistory> findTopByEmailOrderByCreatedDateDesc(String email);
}
