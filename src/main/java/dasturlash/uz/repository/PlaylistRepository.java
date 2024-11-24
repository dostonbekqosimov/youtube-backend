package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Playlist;
import org.springframework.data.repository.CrudRepository;

public interface PlaylistRepository extends CrudRepository<Playlist, String> {
}
