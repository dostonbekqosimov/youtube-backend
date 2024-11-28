package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.video.VideoMediaDTO;
import dasturlash.uz.entity.Attach;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.VideoProcessingException;
import dasturlash.uz.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AttachService {

    private final AttachRepository attachRepository;

    @Value("${attach.upload.folder}")
    private String folderName;

    @Value("${attach.url}")
    private String attachUrl;


    public AttachDTO upload(MultipartFile file) {
        log.info("Starting file upload process for file: {}", file.getOriginalFilename());

        String pathFolder = generateDateBasedFolder();
        String key = UUID.randomUUID().toString();
        String extension = getExtension(file.getOriginalFilename());
        String fullFilePath = saveAttach(file, pathFolder, key, extension);

        String duration = null;
        if (isVideoFile(extension)) {
            log.debug("File is video, extracting metadata");
            duration = extractVideoMetadata(fullFilePath);
        }

        Attach entity = createAttachEntity(file, key, extension, pathFolder, duration);
        log.info("File upload completed successfully. Generated ID: {}", entity.getId());

        return toDTO(entity);
    }


    public String openURL(String fileName) {
        if (fileName == null) {
            log.warn("Attempt to open URL with null fileName");
            return null;
        }

        log.debug("Attempting to open URL for file: {}", fileName);

        if (isExist(fileName)) {
            String url = attachUrl + "/open/" + fileName;
            log.debug("Successfully generated URL: {}", url);
            return url;
        }

        log.warn("File not found: {}", fileName);
        return null;
    }

    public String openStreamURL(String fileName) {
        if (fileName == null) {
            log.warn("Attempt to create URL for streaming with null fileName");
            return null;
        }

        log.debug("Attempting to create URL for streaming for file: {}", fileName);

        if (isExist(fileName)) {
            String url = attachUrl + "/stream/" + fileName;
            log.debug("Successfully generated URL for streaming: {}", url);
            return url;
        }

        log.warn("File not found for streaming: {}", fileName);
        return null;
    }


    public ResponseEntity<Resource> open(String attachId) {
        log.info("Opening file with ID: {}", attachId);

        if (attachId == null) {
            log.error("Null attachId provided 1");
            throw new AppBadRequestException("The given id must not be null");
        }

        Attach entity = getById(attachId);
        String path = getPath(entity);
        Path filePath = Paths.get(path).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                log.error("File not found at path: {}", path);
                throw new DataNotFoundException("File not found: " + entity.getId());
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            log.info("File successfully opened: {}", attachId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            log.error("Error opening file: {}", attachId, e);
            throw new AppBadRequestException("Could not read file: " + attachId);
        }
    }


    public PageImpl<AttachDTO> getAll(int page, int size) {
        log.info("Retrieving all attachments for page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Attach> entityPages = attachRepository.findAll(pageable);
        List<AttachDTO> response = entityPages.stream().map(this::toDTO).toList();

        log.debug("Retrieved {} attachments", response.size());
        return new PageImpl<>(response, pageable, entityPages.getTotalElements());
    }


    public Boolean delete(String id) {
        log.info("Attempting to delete file with ID: {}", id);

        if (id == null) {
            log.error("Null id provided for deletion");
            throw new AppBadRequestException("The given id must not be null");
        }

        Attach entity = getById(id);
        attachRepository.changeVisible(id, Boolean.FALSE);
        File file = new File(getPath(entity));
        boolean isDeleted = false;

        if (file.exists()) {
            isDeleted = file.delete();
            log.info("File deletion result: {}", isDeleted);
        } else {
            log.warn("File not found on disk: {}", id);
        }

        return isDeleted;
    }


    public Attach getById(String id) {
        log.debug("Retrieving attach by ID: {}", id);

        if (id == null) {
            log.error("Null id provided for retrieval");
            throw new AppBadRequestException("The given id must not be null");
        }

        return attachRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("File not found with ID: {}", id);
                    return new DataNotFoundException("File not found with id: " + id);
                });
    }


    public ResponseEntity<Resource> download(String id) {
        log.info("Starting download for file ID: {}", id);

        if (id == null) {
            log.error("Null id provided for download");
            throw new AppBadRequestException("The given id must not be null");
        }

        try {
            Attach entity = getById(id);
            Path filePath = Paths.get(getPath(entity)).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                log.info("File download successful: {}", id);
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getOriginName() + "\"").body(resource);
            } else {
                log.error("Could not read file: {}", id);
                throw new RuntimeException("Could not read the file!");
            }

        } catch (MalformedURLException e) {
            log.error("Error downloading file: {}", id, e);
            throw new RuntimeException("Could not read the file!");
        }
    }


    public MediaUrlDTO getUrlOfMedia(String attachId) {
        log.debug("Getting media URL for ID: {}", attachId);

        if (attachId == null) {
            log.warn("Null attachId provided 2");
            return null;
        }

        MediaUrlDTO mediaUrlDTO = new MediaUrlDTO();
        mediaUrlDTO.setId(attachId);
        mediaUrlDTO.setUrl(openURL(attachId));

        log.debug("Media URL generated successfully for ID: {}", attachId);
        return mediaUrlDTO;
    }


    public VideoMediaDTO getUrlOfVideo(String attachId) {
        log.debug("Getting video URL for ID: {}", attachId);

        if (attachId == null) {
            log.warn("Null attachId provided 3");
            return null;
        }

        VideoMediaDTO videoMediaDTO = new VideoMediaDTO();
        videoMediaDTO.setVideoId(attachId);
        videoMediaDTO.setUrl(openStreamURL(attachId));
        videoMediaDTO.setDuration(getDurationFromEntity(attachId));

        log.debug("Video URL generated successfully for ID: {}", attachId);
        return videoMediaDTO;
    }


    // Private methods below
    private boolean isVideoFile(String extension) {
        log.debug("Checking if file is video. Extension: {}", extension);

        if (extension == null) {
            return false;
        }

        Set<String> primaryFormats = Set.of("mp4", "webm", "mov");
        Set<String> secondaryFormats = Set.of(
                "avi", "mkv", "m4v", "3gp", "wmv", "flv",
                "mpeg", "mpg", "mts", "ts"
        );

        String lowercaseExt = extension.toLowerCase();
        boolean isVideo = primaryFormats.contains(lowercaseExt) ||
                secondaryFormats.contains(lowercaseExt);

        log.debug("Is video file: {}", isVideo);
        return isVideo;
    }


    private String saveAttach(MultipartFile file, String pathFolder, String key, String extension) {
        log.debug("Saving attachment: {}, key: {}", file.getOriginalFilename(), key);

        Path uploadDir = Path.of(folderName);
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fullFileName = key + "." + extension;
            Path fullPath = Paths.get(folderName + "/" + pathFolder + "/" + fullFileName);
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, file.getBytes());

            log.debug("File saved successfully at: {}", fullPath);
            return fullPath.toString();

        } catch (IOException e) {
            log.error("Failed to save file", e);
            throw new RuntimeException("Failed to save video file", e);
        }
    }


    private Attach createAttachEntity(MultipartFile file, String key,
                                      String extension, String pathFolder, String duration) {
        log.debug("Creating attach entity for file: {}", file.getOriginalFilename());

        Attach entity = new Attach();
        entity.setId(key + "." + extension);
        entity.setPath(pathFolder);
        entity.setSize(file.getSize());
        entity.setType(file.getContentType());
        entity.setOriginName(file.getOriginalFilename());
        entity.setDuration(duration);
        entity.setExtension(extension);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());

        Attach saved = attachRepository.save(entity);
        log.debug("Attach entity created with ID: {}", saved.getId());
        return saved;
    }


    private String generateDateBasedFolder() {
        log.debug("Generating date-based folder");

        Calendar cal = Calendar.getInstance();
        String folder = cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" +
                cal.get(Calendar.DATE);

        log.debug("Generated folder: {}", folder);
        return folder;
    }


    private String extractVideoMetadata(String videoFilePath) {
        log.debug("Extracting video metadata from: {}", videoFilePath);

        String ffmpegPath = "ffmpeg";
        String command = ffmpegPath + " -i " + videoFilePath;

        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            log.debug("FFmpeg process executed successfully");
        } catch (IOException e) {
            log.error("Failed to execute FFmpeg command", e);
            throw new RuntimeException(e);
        }

        return getDuration(process);
    }


    private static String getDuration(Process process) {
        log.debug("Getting duration from FFmpeg process");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String duration = null;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration")) {
                    duration = line.split("Duration:")[1].split(",")[0].trim();
                    log.debug("Duration extracted: {}", duration);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error reading FFmpeg output", e);
            throw new RuntimeException(e);
        }

        if (duration == null) {
            log.error("Failed to extract video duration");
            throw new VideoProcessingException("Failed to extract video duration.");
        }
        return duration;
    }


    private String getExtension(String fileName) {
        log.debug("Getting extension for filename: {}", fileName);

        int lastIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(lastIndex + 1);

        log.debug("Extension extracted: {}", extension);
        return extension;
    }


    private AttachDTO toDTO(Attach entity) {
        log.debug("Converting Attach entity to DTO: {}", entity.getId());

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getId());
        attachDTO.setOriginName(entity.getOriginName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setDuration(entity.getDuration());
        attachDTO.setType(entity.getType());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setUrl(openURL(entity.getId()));

        return attachDTO;
    }


    private String getPath(Attach entity) {
        log.debug("Getting full path for entity: {}", entity.getId());

        String path = folderName + "/" + entity.getPath() + "/" + entity.getId();

        log.debug("Full path: {}", path);
        return path;
    }


    private String getDurationFromEntity(String attachId) {
        log.debug("Getting duration for attach ID: {}", attachId);

        String duration = attachRepository.findAttachDurationById(attachId);

        log.debug("Duration found: {}", duration);
        return duration;
    }


    private Boolean isExist(String attachId) {
        log.debug("Checking if attach exists: {}", attachId);

        if (attachId == null) {
            log.warn("Null attachId provided 4");
            return false;
        }

        boolean exists = attachRepository.existsById(attachId);
        log.debug("Attach exists: {}", exists);
        return exists;
    }

    public ResponseEntity<Resource> streamVideo(String attachId, HttpHeaders headers) {
        log.info("Streaming video for attachId: {}", attachId);

        // Validate attachId
        if (attachId == null) {
            log.error("Null attachId provided for streaming");
            throw new AppBadRequestException("The given id must not be null");
        }

        try {
            // Retrieve the Attach entity
            Attach entity = getById(attachId);
            Path filePath = Paths.get(getPath(entity)).normalize();

            // Create a resource from the file
            Resource resource = new UrlResource(filePath.toUri());

            // Check if resource exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                log.error("Could not read file: {}", attachId);
                throw new DataNotFoundException("File not found or not readable");
            }

            // Get file size
            long fileSize = resource.contentLength();

            // Prepare range header
            List<HttpRange> ranges = headers.getRange();

            // Default to full file if no range specified
            if (ranges.isEmpty()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(resource);
            }

            // Process the first range (most video players send only one range)
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(fileSize);
            long end = range.getRangeEnd(fileSize);
            long rangeLength = end - start + 1;

            log.info("Streaming video range: {}-{}/{}", start, end, fileSize);

            // Use Spring's InputStreamResource to support partial content
            InputStreamResource inputStreamResource = new InputStreamResource(
                    new BufferedInputStream(new FileInputStream(resource.getFile())) {
                        {
                            skip(start);
                        }
                    }
            ) {
                @Override
                public long contentLength() {
                    return rangeLength;
                }
            };

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_RANGE,
                            String.format("bytes %d-%d/%d", start, end, fileSize))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength))
                    .body(inputStreamResource);

        } catch (IOException e) {
            log.error("Error streaming video: {}", attachId, e);
            throw new AppBadRequestException("Could not stream video: " + attachId);
        }
    }
}