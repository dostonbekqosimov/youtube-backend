package dasturlash.uz.service.video;

import dasturlash.uz.entity.video.VideoViewRecord;
import dasturlash.uz.repository.VideoViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoViewService {

    private final VideoViewRepository videoViewRepository;

    public void addViewRecord(String videoId, String ipAddress) {
        boolean result = videoViewRepository.existsByVideoIdAndIpAddress(videoId, ipAddress);
        if (!result) {
            VideoViewRecord entity = new VideoViewRecord();
            entity.setVideoId(videoId);
            entity.setIpAddress(ipAddress);
            entity.setCreatedDate(LocalDateTime.now());
            videoViewRepository.save(entity);
        }

    }

}
