package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.*;
import dasturlash.uz.dto.response.video.*;
import dasturlash.uz.service.video.VideoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/videos")
@Slf4j
@Tag(name = "Video Controller", description = "APIs for video management")
public class VideoController {

    private final VideoService videoService;

    @PostMapping("")
    @Operation(summary = "Create a new video", description = "Endpoint to create a new video with provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoCreateResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<VideoCreateResponseDTO> createVideo(
            @Parameter(description = "Video creation details", required = true)
            @RequestBody @Valid VideoCreateDTO dto) {
        log.info("Entering createVideo with request: {}", dto);
        ResponseEntity<VideoCreateResponseDTO> response = ResponseEntity.ok(videoService.createVideo(dto));
        log.info("Exiting createVideo with response: {}", response);
        return response;
    }

    @GetMapping("/edit")
    @Operation(summary = "Get video update information", description = "Retrieve video details for updating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video update information retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> getVideoUpdateInfo(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @RequestParam("v") String videoId) {
        log.info("Entering getVideoUpdateInfo with videoId: {}", videoId);
        VideoUpdateDTO updateDTO = videoService.getUpdateInfo(videoId);
        log.info("Exiting getVideoUpdateInfo with response: {}", updateDTO);
        return ResponseEntity.ok().body(updateDTO);
    }

    @PutMapping("/{videoId}")
    @Operation(summary = "Update video details", description = "Update information for an existing video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> updateVideo(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @PathVariable String videoId,
            @Parameter(description = "Updated video details", required = true)
            @Valid @RequestBody VideoUpdateDTO dto) {
        log.info("Entering updateVideo with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateVideo(videoId, dto));
        log.info("Exiting updateVideo with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/status")
    @Operation(summary = "Update video status", description = "Change the status of an existing video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video status updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> updateStatus(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @PathVariable String videoId,
            @Parameter(description = "New video status", required = true)
            @Valid @RequestBody VideoStatusDTO dto) {
        log.info("Entering updateStatus with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateStatus(videoId, dto));
        log.info("Exiting updateStatus with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/playlist")
    @Operation(summary = "Update video playlist", description = "Change the playlist of an existing video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video playlist updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid playlist"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> updatePlaylist(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @PathVariable String videoId,
            @Parameter(description = "New playlist details", required = true)
            @Valid @RequestBody VideoPlaylistDTO dto) {
        log.info("Entering updatePlaylist with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updatePlaylist(videoId, dto));
        log.info("Exiting updatePlaylist with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/category")
    @Operation(summary = "Update video category", description = "Change the category of an existing video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video category updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> updateCategory(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @PathVariable String videoId,
            @Parameter(description = "New category details", required = true)
            @Valid @RequestBody VideoCategoryDTO dto) {
        log.info("Entering updateCategory with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updateCategory(videoId, dto));
        log.info("Exiting updateCategory with response: {}", response);
        return response;
    }

    @PatchMapping("/{videoId}/preview")
    @Operation(summary = "Update video preview", description = "Change the preview of an existing video")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video preview updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoUpdateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid preview"),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoUpdateDTO> updatePreview(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @PathVariable String videoId,
            @Parameter(description = "New preview details", required = true)
            @Valid @RequestBody VideoPreviewDTO dto) {
        log.info("Entering updatePreview with videoId: {} and request: {}", videoId, dto);
        ResponseEntity<VideoUpdateDTO> response = ResponseEntity.ok(videoService.updatePreview(videoId, dto));
        log.info("Exiting updatePreview with response: {}", response);
        return response;
    }

    @GetMapping("/watch")
    @Operation(summary = "Watch video", description = "Retrieve full video information for watching")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video information retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoFullInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoFullInfoDTO> watchVideo(
            @Parameter(description = "Video ID", required = true, example = "video123")
            @RequestParam("v") String videoId) {
        log.info("Entering watchVideo with videoId: {}", videoId);
        ResponseEntity<VideoFullInfoDTO> response = ResponseEntity.ok(videoService.getVideoById(videoId));
        log.info("Exiting watchVideo with response: OK");
        return response;
    }

    @GetMapping("/category")
    @Operation(summary = "Get videos by category", description = "Retrieve a paginated list of videos for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoShortInfoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category or pagination parameters")
    })
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByCategoryId(
            @Parameter(description = "Category ID", required = true, example = "123")
            @RequestParam("categoryId") Long categoryId,
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByCategoryId with categoryId: {}", categoryId);
        return ResponseEntity.ok().body(videoService.getVideoListByCategoryId(page - 1, size, categoryId));
    }

    @GetMapping("/title")
    @Operation(summary = "Search videos by title", description = "Retrieve a paginated list of videos matching the given title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoShortInfoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid title or pagination parameters")
    })
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByTitle(
            @Parameter(description = "Video title to search", required = true, example = "Tutorial")
            @RequestParam("title") String title,
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByTitle with title: {}", title);
        return ResponseEntity.ok().body(videoService.getVideoListByTitle(page - 1, size, title));
    }

    @GetMapping("/tag")
    @Operation(summary = "Get videos by tag", description = "Retrieve a paginated list of videos for a specific tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoShortInfoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid tag or pagination parameters")
    })
    public ResponseEntity<PageImpl<VideoShortInfoDTO>> getVideoListByTagName(
            @Parameter(description = "Tag name to search", required = true, example = "programming")
            @RequestParam("tagName") String tagName,
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByTagName with tagName: {}", tagName);
        return ResponseEntity.ok().body(videoService.getVideoListByTagName(tagName, page - 1, size));
    }

    @GetMapping("/channel/{channelId}")
    @Operation(summary = "Get videos by channel", description = "Retrieve a paginated list of videos for a specific channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VideoPlayListInfoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid channel or pagination parameters"),
            @ApiResponse(responseCode = "404", description = "Channel not found")
    })
    public ResponseEntity<PageImpl<VideoPlayListInfoDTO>> getVideoListByChannelId(
            @Parameter(description = "Channel ID", required = true, example = "channel123")
            @PathVariable("channelId") String channelId,
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("Entering getVideoListByChannelId with channelId: {}", channelId);
        return ResponseEntity.ok().body(videoService.getChannelVideoListByChannelId(page - 1, size, channelId));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get admin video list", description = "Retrieve a paginated list of videos for admin management")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdminVideoInfoDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Page<AdminVideoInfoDTO>> getAdminVideoList(
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        log.info("Entering getAdminVideoList with page: {} and size: {}", page, size);
        Page<AdminVideoInfoDTO> videoList = videoService.getAdminVideoList(page - 1, size);
        log.info("Exiting getAdminVideoList with total videos: {}", videoList.getTotalElements());
        return ResponseEntity.ok(videoList);
    }

    @DeleteMapping("/{videoId}")
    @Operation(summary = "Delete video by ID", description = "Delete a video using its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video deleted successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "404", description = "Video not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<String> deleteVideoById(
            @Parameter(description = "Video ID to delete", required = true, example = "video123")
            @PathVariable("videoId") String videoId) {
        log.info("Entering deleteVideoById with videoId: {}", videoId);
        String result = videoService.deleteVideoById(videoId);
        log.info("Exiting deleteVideoById with result: {}", result);
        return ResponseEntity.ok(result);
    }
}