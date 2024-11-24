package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.*;
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

    @PutMapping("/{videoId}")
    public ResponseEntity<VideoDTO> updateVideo(
            @PathVariable String videoId,
            @Valid @RequestBody VideoUpdateDTO dto) {
        return ResponseEntity.ok(videoService.updateVideo(videoId, dto));
    }

    @PatchMapping("/{videoId}/status")
    public ResponseEntity<VideoDTO> updateVisibility(
            @PathVariable String videoId,
            @Valid @RequestBody VideoStatusDTO dto) {
        return ResponseEntity.ok(videoService.updateVisibility(videoId, dto));
    }

    @PatchMapping("/{videoId}/playlist")
    public ResponseEntity<VideoDTO> updatePlaylist(
            @PathVariable String videoId,
            @Valid @RequestBody VideoPlaylistDTO dto) {
        return ResponseEntity.ok(videoService.updatePlaylist(videoId, dto));
    }

    @PatchMapping("/{videoId}/category")
    public ResponseEntity<VideoDTO> updateCategory(
            @PathVariable String videoId,
            @Valid @RequestBody VideoCategoryDTO dto) {
        return ResponseEntity.ok(videoService.updateCategory(videoId, dto));
    }

    @PatchMapping("/{videoId}/preview")
    public ResponseEntity<VideoDTO> updateThumbnail(
            @PathVariable String videoId,
            @Valid @RequestBody VideoPreviewDTO dto) {
        return ResponseEntity.ok(videoService.updateThumbnail(videoId, dto));
    }

    @GetMapping("/watch")
    public ResponseEntity<VideoDTO> watchVideo(@RequestParam("v") String videoId) {
        return ResponseEntity.ok(videoService.getVideoById(videoId));
    }

}
