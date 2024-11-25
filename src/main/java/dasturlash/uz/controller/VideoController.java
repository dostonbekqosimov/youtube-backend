package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.*;
import dasturlash.uz.dto.response.VideShortInfoDTO;
import dasturlash.uz.dto.response.video.VideoCreateResponseDTO;
import dasturlash.uz.dto.response.video.VideoFullInfoDTO;
import dasturlash.uz.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/videos")
@Slf4j
public class VideoController {

    private final VideoService videoService;

    @PostMapping("")
    public ResponseEntity<VideoCreateResponseDTO> createVideo(@RequestBody @Valid VideoCreateDTO dto) {
        log.info("Entering createVideo with request: {}", dto);
        ResponseEntity<VideoCreateResponseDTO> response = ResponseEntity.ok(videoService.createVideo(dto));
        log.info("Exiting createVideo with response: {}", response);
        return response;
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<VideoUpdateDTO> updateVideo(
            @PathVariable String videoId,
            @Valid @RequestBody VideoUpdateDTO dto) {
        log.info("Entering updateVideo with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateVideo(videoId, dto));
        log.info("Exiting updateVideo with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/status")
    public ResponseEntity<VideoUpdateDTO> updateStatus(
            @PathVariable String videoId,
            @Valid @RequestBody VideoStatusDTO dto) {
        log.info("Entering updateStatus with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateStatus(videoId, dto));
        log.info("Exiting updateStatus with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/playlist")
    public ResponseEntity<VideoUpdateDTO> updatePlaylist(
            @PathVariable String videoId,
            @Valid @RequestBody VideoPlaylistDTO dto) {
        log.info("Entering updatePlaylist with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updatePlaylist(videoId, dto));
        log.info("Exiting updatePlaylist with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/category")
    public ResponseEntity<VideoUpdateDTO> updateCategory(
            @PathVariable String videoId,
            @Valid @RequestBody VideoCategoryDTO dto) {
        log.info("Entering updateCategory with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateCategory(videoId, dto));
        log.info("Exiting updateCategory with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/preview")
    public ResponseEntity<VideoUpdateDTO> updatePreview(
            @PathVariable String videoId,
            @Valid @RequestBody VideoPreviewDTO dto) {
        log.info("Entering updatePreview with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updatePreview(videoId, dto));
        log.info("Exiting updatePreview with response: {}", response);
        return response;
    }

    @GetMapping("/watch")
    public ResponseEntity<VideoFullInfoDTO> watchVideo(@RequestParam("v") String videoId) {
        log.info("Entering watchVideo with videoId: {}", videoId);
        ResponseEntity<VideoFullInfoDTO> response = ResponseEntity.ok(videoService.getVideoById(videoId));
        log.info("Exiting watchVideo with response: {}", response);
        return response;
    }

//    @GetMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<VideShortInfoDTO>


}
