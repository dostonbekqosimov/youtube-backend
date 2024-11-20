package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.entity.Attach;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.VideoProcessingException;
import dasturlash.uz.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
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



    public AttachDTO videoUpload(MultipartFile file) {
        // Generate a unique path based on current date
        String pathFolder = generateDateBasedFolder();

        // Generate a unique key for the file
        String key = UUID.randomUUID().toString();

        // Extract file extension
        String extension = getExtension(file.getOriginalFilename());

        // Save the file and get the full path
        String fullFilePath = saveVideoFile(file, pathFolder, key, extension);

        // Extract video metadata (duration)
        String duration = extractVideoMetadata(fullFilePath);

        // Create and save Attach entity
        Attach entity = createAttachEntity(file, key, extension, pathFolder, duration);

        // Convert to DTO and return
        return toDTO(entity);
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

    public ResponseEntity<Resource> openVideo(String attachId) {
        // Retrieve the file entity from the database using the given filename
        Attach entity = getById(attachId);

        // Construct the full file path by combining base folder, date path, and file name
        String path = folderName + "/" + entity.getPath() + "/" + entity.getId();

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


    public Attach getById(String id) {
        // Use Spring Data JPA's findById method with a custom exception if not found
        return attachRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("File not found with id: " + id));
    }
}