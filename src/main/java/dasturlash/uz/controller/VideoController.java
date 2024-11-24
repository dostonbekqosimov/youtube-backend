package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.VideoCreateDTO;
import dasturlash.uz.dto.response.video.VideoCreateResponseDTO;
import dasturlash.uz.dto.response.video.VideoDTO;
import dasturlash.uz.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/videos")
public class VideoController {

    private final VideoService videoService;

    @PostMapping("")
    public ResponseEntity<VideoCreateResponseDTO> createVideo(@RequestBody @Valid VideoCreateDTO dto) {
        return ResponseEntity.ok(videoService.createVideo(dto));
    }

    @GetMapping("/watch")
    public ResponseEntity<VideoDTO> watchVideo(@RequestParam("v") String videoId) {
        return ResponseEntity.ok(videoService.getVideoById(videoId));
    }

}
