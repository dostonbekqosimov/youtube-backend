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
public class AttachController {

    private static Logger log = LoggerFactory.getLogger(AttachController.class);

    private final AttachService attachService;

    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> videoUpload(@RequestParam("file") MultipartFile file) {
        log.warn("New file uploaded: {}", file.getOriginalFilename());
        return ResponseEntity.ok().body(attachService.videoUpload(file));

    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> openVideo(@PathVariable("fileName") String fileName) {
        System.out.println("I'm being called with: " + fileName);
        return attachService.openVideo(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadVideo(@PathVariable("fileName") String fileName) {
        return attachService.downloadVideo(fileName);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<AttachDTO>> getAllVideos(@RequestParam(value = "page", defaultValue = "1") int page,
                                                            @RequestParam(value = "size", defaultValue = "15") int size) {
        return ResponseEntity.ok(attachService.getAll(page - 1, size));
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> deleteVideo(@PathVariable("fileName") String id) {
        return ResponseEntity.ok(attachService.delete(id));
    }

}
