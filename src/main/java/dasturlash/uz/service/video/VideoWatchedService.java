package dasturlash.uz.service.video;

import dasturlash.uz.entity.video.VideoWatched;
import dasturlash.uz.repository.VideoWatchedRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import dasturlash.uz.util.UserInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VideoWatchedService {
    private final VideoWatchedRepository repository;
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
        LocalDateTime now = LocalDateTime.now().minusMinutes(2);

        if (watchedByVideoId != null) {
            if (watchedByVideoId.getCreatedDate().isAfter(now) &&
                    watchedByVideoId.getIpAddress().equals(user.getIpAddress()) &&
                    watchedByVideoId.getUserBrowser().equals(user.getUserAgent()) &&
                    watchedByVideoId.getVideoId().equals(videoId)&&
                    watchedByVideoId.getProfileId() == user.getUserId()) {
                return false;
            }
        }
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



}
