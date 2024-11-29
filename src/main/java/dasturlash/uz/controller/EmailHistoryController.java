package dasturlash.uz.controller;

import dasturlash.uz.entity.EmailHistory;
import dasturlash.uz.service.email.EmailHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/emailHistory")
@RequiredArgsConstructor
public class EmailHistoryController {


    private final EmailHistoryService emailHistoryService;

    // Get EmailHistory by email
    @GetMapping("/by-email")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EmailHistory>> getEmailHistoryByEmail(@RequestParam String email) {
        List<EmailHistory> emailHistoryList = emailHistoryService.getEmailHistoryByEmail(email);
        if (emailHistoryList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(emailHistoryList);
    }

    // Get EmailHistory by given date
    @GetMapping("/by-date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EmailHistory>> getEmailHistoryByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<EmailHistory> emailHistoryList = emailHistoryService.getEmailHistoryByDate(date);
        if (emailHistoryList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(emailHistoryList);
    }

    // Get EmailHistory with pagination (Admin)
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<EmailHistory>> getEmailHistoryPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<EmailHistory> emailHistoryPage = emailHistoryService.getEmailHistoryWithPagination(pageable);

        if (emailHistoryPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(emailHistoryPage);
    }
}

