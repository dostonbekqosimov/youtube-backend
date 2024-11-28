package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.service.AttachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attachment Management", description = "APIs for managing file attachments")
public class AttachController {

    private final AttachService attachService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file", description = "Uploads a single file and returns its metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AttachDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file upload"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AttachDTO> upload(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        log.warn("New file uploaded: {}", file.getOriginalFilename());
        log.info("File size: {} bytes", file.getSize());
        AttachDTO result = attachService.upload(file);
        log.info("File successfully uploaded with ID: {}", result.getId());
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/open/{attachId}")
    @Operation(summary = "Open a file", description = "Retrieves a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File opened successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "400", description = "Invalid file ID")
    })
    public ResponseEntity<Resource> open(
            @Parameter(description = "ID of the file to open", required = true)
            @PathVariable("attachId") String attachId) {
        log.info("Attempting to open file: {}", attachId);
        ResponseEntity<Resource> response = attachService.open(attachId);
        log.info("File {} opened successfully", attachId);
        return response;
    }

    @GetMapping("/download/{attachId}")
    @Operation(summary = "Download a file", description = "Downloads a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "400", description = "Invalid file ID")
    })
    public ResponseEntity<Resource> download(
            @Parameter(description = "ID of the file to download", required = true)
            @PathVariable("attachId") String attachId) {
        log.info("Download request received for file: {}", attachId);
        ResponseEntity<Resource> response = attachService.download(attachId);
        log.info("File {} downloaded successfully", attachId);
        return response;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all attachments", description = "Retrieves a paginated list of all attachments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageImpl.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PageImpl<AttachDTO>> getAllAttaches(
            @Parameter(description = "Page number", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "15")
            @RequestParam(value = "size", defaultValue = "15") int size) {
        log.info("Fetching attachments page {} with size {}", page, size);
        PageImpl<AttachDTO> result = attachService.getAll(page - 1, size);
        log.info("Retrieved {} attachments", result.getContent().size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{attachId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a file", description = "Deletes a file by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Boolean> deleteVideo(
            @Parameter(description = "ID of the file to delete", required = true)
            @PathVariable("attachId") String attachId) {
        log.info("Attempting to delete file with ID: {}", attachId);
        Boolean result = attachService.delete(attachId);
        if (result) {
            log.info("File with ID {} successfully deleted", attachId);
        } else {
            log.warn("Failed to delete file with ID {}", attachId);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stream/{attachId}")
    @Operation(summary = "Stream a video", description = "Streams a video file by its ID with support for partial content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video streaming started successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "206", description = "Partial content returned"),
            @ApiResponse(responseCode = "404", description = "Video not found"),
            @ApiResponse(responseCode = "400", description = "Invalid video ID")
    })
    public ResponseEntity<Resource> streamVideo(
            @Parameter(description = "ID of the video to stream", required = true)
            @PathVariable String attachId,
            @Parameter(hidden = true)
            @RequestHeader HttpHeaders headers
    ) {
        return attachService.streamVideo(attachId, headers);
    }
}