package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Playlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PlaylistRepository extends CrudRepository<Playlist, String> {

    @Query("select ch.profileId from Playlist p join p.channel ch where p.id = ?1")
    Long checkOwner(String playlistId);
}
