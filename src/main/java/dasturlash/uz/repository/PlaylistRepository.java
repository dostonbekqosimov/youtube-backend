package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Playlist;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlaylistRepository extends CrudRepository<Playlist, String>, JpaRepository<Playlist, String> {

    @Query("select ch.profileId from Playlist p join p.channel ch where p.id = ?1")
    Long checkOwner(String playlistId);

    @Modifying
    @Transactional
    @Query("update Playlist p set p.visible = false where  p.id = ?1")
    void setVisibleFalse(String playlistId);

    @Query("from Playlist p where p.channelId = ?1")
    List<Playlist> findAllPlaylistOwner(String channelId);
}
