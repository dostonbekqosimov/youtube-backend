package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.response.ChannelMediaDTO;
import dasturlash.uz.entity.Attach;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.VideoProcessingException;
import dasturlash.uz.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class AttachService {

    // Attach Service A

    private final AttachRepository attachRepository;


    @Value("${attach.upload.folder}")
    private String folderName;
    @Value("${attach.url}")
    private String attachUrl;


    public AttachDTO upload(MultipartFile file) {
        // Generate a unique path based on current date
        String pathFolder = generateDateBasedFolder();

        // Generate a unique key for the file
        String key = UUID.randomUUID().toString();

        // Extract file extension
        String extension = getExtension(file.getOriginalFilename());

        // Save the file and get the full path
        String fullFilePath = saveVideoFile(file, pathFolder, key, extension);

        String duration = null;
        if (isVideoFile(extension)) {
            duration = extractVideoMetadata(fullFilePath);
        }

        // Create and save Attach entity
        Attach entity = createAttachEntity(file, key, extension, pathFolder, duration);

        // Convert to DTO and return
        return toDTO(entity);
    }

    private boolean isVideoFile(String extension) {
        if (extension == null) {
            return false;
        }

        // Primary formats (most common and recommended)
        Set<String> primaryFormats = Set.of(
                "mp4",    // Most universal format, widely supported
                "webm",   // Open format, great for web streaming
                "mov"     // Common format from iOS devices
        );

        // Secondary formats (supported but might need transcoding)
        Set<String> secondaryFormats = Set.of(
                "avi",    // Older but still common format
                "mkv",    // Popular container format
                "m4v",    // MPEG-4 video
                "3gp",    // Mobile device videos
                "wmv",    // Windows Media Video
                "flv",    // Legacy Flash format
                "mpeg",   // Older standard format
                "mpg",    // Older standard format
                "mts",    // HD camera format
                "ts"      // Transport stream
        );

        String lowercaseExt = extension.toLowerCase();
        return primaryFormats.contains(lowercaseExt) ||
                secondaryFormats.contains(lowercaseExt);
    }

    // All video upload related methods

    private String saveVideoFile(MultipartFile file, String pathFolder, String key, String extension) {

        // Ensure upload directory exists
        Path uploadDir = Path.of(folderName);
        try {
            // Create directory if it doesn't exist
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Construct full file path
            String fullFileName = key + "." + extension;
            Path fullPath = Paths.get(folderName + "/" + pathFolder + "/" + fullFileName);

            // Create parent directories if they don't exist
            Files.createDirectories(fullPath.getParent());

            // Write file bytes
            Files.write(fullPath, file.getBytes());

            // Return full file path as a string
            return fullPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save video file", e);
        }
    }


    private Attach createAttachEntity(MultipartFile file, String key,
                                      String extension, String pathFolder, String duration) {
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

        // Save and return the entity
        return attachRepository.save(entity);
    }


    private String generateDateBasedFolder() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;  // Months are 0-indexed
        int day = cal.get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    private String extractVideoMetadata(String videoFilePath) {

        // Full path to FFmpeg (it should be available in your PATH)
        String ffmpegPath = "ffmpeg";  // If FFmpeg is not in PATH, specify its full path.

        // Run FFmpeg command to get video metadata, especially duration
        String command = ffmpegPath + " -i " + videoFilePath;

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Information about the video: " + process);
        System.out.println(process.getErrorStream().toString());

        return getDuration(process);
    }

    private static String getDuration(Process process) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        String duration = null;

        // Read the FFmpeg output and extract the duration
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line.contains("Duration")) {
                duration = line.split("Duration:")[1].split(",")[0].trim();
                break;
            }
        }

        // Handle the case where no duration is found
        if (duration == null) {
            throw new VideoProcessingException("Failed to extract video duration.");
        }
        return duration;
    }

    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public String openURL(String fileName) {
        return attachUrl + "/open/" + fileName;
    }

    private AttachDTO toDTO(Attach entity) {

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

    public ResponseEntity<Resource> open(String attachId) {
        // Retrieve the file entity from the database using the given filename
        Attach entity = getById(attachId);

        // Construct the full file path by combining base folder, date path, and file name
        String path = getPath(entity);

        // Normalize the file path to handle any potential path manipulation
        Path filePath = Paths.get(path).normalize();

        Resource resource = null;
        try {
            // Convert the file path to a URL resource for streaming
            resource = new UrlResource(filePath.toUri());

            // Check if the resource actually exists
            if (!resource.exists()) {
                throw new DataNotFoundException("File not found: " + entity.getId());
            }

            // Attempt to determine the content type of the file
            String contentType = Files.probeContentType(filePath);

            // Fallback to generic binary stream if content type cannot be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Return a ResponseEntity with the file resource and appropriate content type
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            // Wrap any IO errors in a custom exception for consistent error handling
            throw new AppBadRequestException("Could not read file: " + attachId);
        }
    }

    public PageImpl<AttachDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attach> entityPages = attachRepository.findAll(pageable);
        List<AttachDTO> response = entityPages.stream().map(this::toDTO).toList();
        return new PageImpl<>(response, pageable, entityPages.getTotalElements());
    }

    public Boolean delete(String id) {
        Attach entity = getById(id);
//        attachRepository.delete(entity);
        attachRepository.changeVisible(id, Boolean.FALSE);
        File file = new File(getPath(entity));
        boolean isDeleted = false;
        if (file.exists()) {
            isDeleted = file.delete();
        }
        return isDeleted;
    }

    public Attach getById(String id) {
        // Use Spring Data JPA's findById method with a custom exception if not found
        return attachRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("File not found with id: " + id));
    }

    public ResponseEntity<Resource> download(String id) {

        try {
            Attach entity = getById(id);
            Path filePath = Paths.get(getPath(entity)).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + entity.getOriginName() + "\"").body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read the file!");
        }
    }

    private String getPath(Attach entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getId();
    }

    public ChannelMediaDTO getUrlOfMedia(String attachId) {
        if (attachId == null) {
            return null;
        }
        ChannelMediaDTO channelMediaDTO = new ChannelMediaDTO();
        channelMediaDTO.setId(attachId);
        channelMediaDTO.setUrl(openURL(attachId));
        return channelMediaDTO;
    }

    public AttachDTO updateProfileAttach(MultipartFile file) {
        String pathFolder = generateDateBasedFolder();
        String key = UUID.randomUUID().toString();
        String extension = getExtension(file.getOriginalFilename());
        String fullPath = saveVideoFile(file, pathFolder, key, extension);

        Attach entity = new Attach();
        entity.setId(key + "." + extension);
        entity.setPath(pathFolder);
        entity.setSize(file.getSize());
        entity.setType(file.getContentType());
        entity.setOriginName(file.getOriginalFilename());entity.setExtension(extension);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        attachRepository.save(entity);
        return toDTO(entity);
    }


}
