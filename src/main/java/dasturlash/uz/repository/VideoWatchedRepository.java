package dasturlash.uz.repository;

import dasturlash.uz.entity.video.VideoWatched;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoWatchedRepository extends CrudRepository<VideoWatched, String> {


    @Query("from VideoWatched v where v.videoId = ?1 and v.ipAddress = ?2 and v.userBrowser = ?3 order by v.createdDate desc limit 1")
    VideoWatched findByVideoIdAndIpAndBrowser(String videoId, String ipAddress, String userAgent);

    @Query("from VideoWatched v where v.videoId = ?1 and v.ipAddress = ?2 and v.userBrowser = ?3 and v.profileId = ?4 order by v.createdDate desc limit 1")
    VideoWatched findByVideoIdAndIpAndBrowserAndProfileId(String videoId, String ipAddress, String userAgent, Long userId);

    @Query("select vw.videoId as videoId from VideoWatched vw where vw.profileId = ?1")
    List<String> findByProfileId(Long profileId);
}
