package dasturlash.uz.repository;

import dasturlash.uz.entity.video.PlaylistVideo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlaylistVideoRepository extends CrudRepository<PlaylistVideo, String> {

    boolean existsByPlaylistIdAndVideoId(String playlistId, String videoId);

    @Transactional
    @Modifying
    @Query("delete from PlaylistVideo pv where pv.videoId = ?1 and pv.playlistId in ?2")
    int deleteByPlaylistIdAndVideoId(String videoId, List<String> playlistId);
}
