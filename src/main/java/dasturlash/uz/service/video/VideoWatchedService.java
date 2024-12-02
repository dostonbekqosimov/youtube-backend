package dasturlash.uz.service.video;

import dasturlash.uz.dto.response.video.VideoHistoryDTO;
import dasturlash.uz.entity.Attach;
import dasturlash.uz.entity.video.VideoWatched;
import dasturlash.uz.mapper.VideoWatchedHistory;
import dasturlash.uz.repository.VideoWatchedRepository;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

import dasturlash.uz.service.AttachService;
import dasturlash.uz.util.UserInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VideoWatchedService {
    private final VideoWatchedRepository repository;
    @Lazy
    @Autowired
    private VideoService videoService;
    private final AttachService attachService;
    private static Logger log = LoggerFactory.getLogger(VideoWatchedService.class);

    public void addHistoryWatch(String videoId, UserInfoUtil user, HttpServletRequest userInfo) {
        log.info("Adding history watch for video id {} profile id {}", videoId, user);

        if (checkTime(videoId, user)) {
            VideoWatched videoWatched = new VideoWatched();
            videoWatched.setVideoId(videoId);
            videoWatched.setProfileId(user.getUserId());
            videoWatched.setIpAddress(user.getIpAddress());
            videoWatched.setUserBrowser(user.getUserAgent());
            videoWatched.setCreatedDate(LocalDateTime.now());
            repository.save(videoWatched);
            log.info("Added history watch for video id {}", videoId);
            return;
        }
        log.info("This user has seen this video before.");
    }

    //Shu user shu videoni korganligini va qachon korganligini tekshirish
    public boolean checkTime(String videoId, UserInfoUtil user) {
        VideoWatched watchedByVideoId = getWatchedByVideoId(videoId, user);
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);

        if (watchedByVideoId != null) {
            if (watchedByVideoId.getCreatedDate().isAfter(now) &&
                    watchedByVideoId.getIpAddress().equals(user.getIpAddress()) &&
                    watchedByVideoId.getUserBrowser().equals(user.getUserAgent()) &&
                    watchedByVideoId.getVideoId().equals(videoId)&&
                    watchedByVideoId.getProfileId() == user.getUserId()) {
                return false;
            }
        }
        if (watchedByVideoId != null) {repository.delete(watchedByVideoId);}
        return true;
    }
    
    public VideoWatched getWatchedByVideoId(String videoId, UserInfoUtil user) {
        String ipAddress = user.getIpAddress();
        String browser = user.getUserAgent();
        Long userId = user.getUserId();
        VideoWatched watched = null;

        if (userId == null) {
            watched = repository.findByVideoIdAndIpAndBrowser(videoId, ipAddress, browser);
            return watched;
        }

        watched = repository.findByVideoIdAndIpAndBrowserAndProfileId(videoId, ipAddress, browser, userId);

        if (watched == null) {
            return null;
        }

        return watched;
    }


    public List<VideoHistoryDTO> getHistory() {
        Long currentUserId = getCurrentUserId();
        List<VideoHistoryDTO> listDTO = new ArrayList<>();

        for (String videoId : repository.findByProfileId(currentUserId)) {
            VideoHistoryDTO videoHistoryDTO = toVideoHistoryDTO(videoService.getVideoWatchedHistory(videoId));
            listDTO.add(videoHistoryDTO);
        }
    return listDTO;
    }

    public VideoHistoryDTO toVideoHistoryDTO(VideoWatchedHistory history) {
        VideoHistoryDTO dto = new VideoHistoryDTO();

        dto.setTitle(history.getTitle());
        dto.setChannelName(history.getChannelName());
        dto.setViewCount(history.getViewCount());
        dto.setDuration(attachService.getDurationFromEntity(history.getAttachId()));
        dto.setUrl(attachService.openURL(history.getAttachId()));
        return dto;
    }
}
