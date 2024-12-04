package dasturlash.uz.repository;

import dasturlash.uz.entity.video.PlaylistVideo;
import org.springframework.data.repository.CrudRepository;

public interface PlaylistVideoRepository extends CrudRepository<PlaylistVideo, String> {

    boolean existsByPlaylistIdAndVideoId(String playlistId, String videoId);
}
