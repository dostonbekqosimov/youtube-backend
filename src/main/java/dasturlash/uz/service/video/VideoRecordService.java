package dasturlash.uz.service.video;

import dasturlash.uz.entity.video.VideoShareRecord;
import dasturlash.uz.entity.video.VideoViewRecord;
import dasturlash.uz.repository.VideoShareRepository;
import dasturlash.uz.repository.VideoViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoRecordService {

    private final VideoViewRepository videoViewRepository;
    private final VideoShareRepository videoShareRepository;

    public void addViewRecord(String videoId, String ipAddress, Long currentUserId) {
        boolean result = videoViewRepository.existsByVideoIdAndIpAddress(videoId, ipAddress);
        if (!result) {
            VideoViewRecord entity = new VideoViewRecord();
            entity.setVideoId(videoId);
            entity.setIpAddress(ipAddress);

            if (currentUserId != null) {
                entity.setProfileId(currentUserId);
            }

            entity.setCreatedDate(LocalDateTime.now());
            videoViewRepository.save(entity);
        }

    }


    public void increaseShareCount(String videoId, String ipAddress, Long currentUserId) {
        VideoShareRecord entity = new VideoShareRecord();
        entity.setVideoId(videoId);
        entity.setIpAddress(ipAddress);

        if (currentUserId != null) {
            entity.setProfileId(currentUserId);
        }
        entity.setCreatedDate(LocalDateTime.now());
        videoShareRepository.save(entity);

    }

}
