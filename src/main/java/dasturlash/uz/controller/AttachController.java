package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
@Slf4j
public class AttachController {


    private final AttachService attachService;

    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> upload(@RequestParam("file") MultipartFile file) {
        log.warn("New file uploaded: {}", file.getOriginalFilename());
        log.info("File size: {} bytes", file.getSize());
        AttachDTO result = attachService.upload(file);
        log.info("File successfully uploaded with ID: {}", result.getId());
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> open(@PathVariable("fileName") String fileName) {
        log.info("Attempting to open file: {}", fileName);
        ResponseEntity<Resource> response = attachService.open(fileName);
        log.info("File {} opened successfully", fileName);
        return response;
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) {
        log.info("Download request received for file: {}", fileName);
        ResponseEntity<Resource> response = attachService.download(fileName);
        log.info("File {} downloaded successfully", fileName);
        return response;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<AttachDTO>> getAllAttaches(@RequestParam(value = "page", defaultValue = "1") int page,
                                                              @RequestParam(value = "size", defaultValue = "15") int size) {
        log.info("Fetching attachments page {} with size {}", page, size);
        PageImpl<AttachDTO> result = attachService.getAll(page - 1, size);
        log.info("Retrieved {} attachments", result.getContent().size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> deleteVideo(@PathVariable("fileName") String id) {
        log.info("Attempting to delete file with ID: {}", id);
        Boolean result = attachService.delete(id);
        if (result) {
            log.info("File with ID {} successfully deleted", id);
        } else {
            log.warn("Failed to delete file with ID {}", id);
        }
        return ResponseEntity.ok(result);
    }

}