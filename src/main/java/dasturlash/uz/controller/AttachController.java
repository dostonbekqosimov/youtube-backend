package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

    @PostMapping("/upload")
    public ResponseEntity<AttachDTO> videoUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("I'm being called with: " + file.getOriginalFilename());
        return ResponseEntity.ok().body(attachService.videoUpload(file));

    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> openVideo(@PathVariable("fileName") String fileName) {
        System.out.println("I'm being called with: " + fileName);
        return attachService.openVideo(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) {
        return attachService.downloadVideo(fileName);
    }



}
