package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.*;

import dasturlash.uz.dto.response.video.*;
import dasturlash.uz.service.video.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @GetMapping("/edit")
    public ResponseEntity<VideoUpdateDTO> getVideoUpdateInfo(
            @RequestParam("v") String videoId) {
        VideoUpdateDTO updateDTO = videoService.getUpdateInfo(videoId);
        return ResponseEntity.ok().body(updateDTO);
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
        log.info("Exiting watchVideo with response: {}", "ok");
        return response;
    }

    @GetMapping("/category")
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByCategoryId(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByCategoryId with categoryId: {}", categoryId);
        return ResponseEntity.ok().body(videoService.getVideoListByCategoryId(page - 1, size, categoryId));

    }

    @GetMapping("/title")
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByTitle(
            @RequestParam("title") String title,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByCategoryId with title: {}", title);
        return ResponseEntity.ok().body(videoService.getVideoListByTitle(page - 1, size, title));

    }

    // after tag implemented, we will finish this [done]
    @GetMapping("/tag")
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByTagName(
            @RequestParam("tagName") String tagName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByTagName with tagName: {}", tagName);
        return ResponseEntity.ok().body(videoService.getVideoListByTagName(tagName, page - 1, size));
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<PageImpl<VideoPlayListInfoDTO>> getVideoListByChannelId(
            @PathVariable("channelId") String channelId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByChannelId with channelId: {}", channelId);
        return ResponseEntity.ok().body(videoService.getChannelVideoListByChannelId(page - 1, size, channelId));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<AdminVideoInfoDTO>> getVideosByCategory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<AdminVideoInfoDTO> videoList = videoService.getAdminVideoList(page - 1, size);
        return ResponseEntity.ok(videoList);
    }


}
