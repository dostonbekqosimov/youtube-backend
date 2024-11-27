package dasturlash.uz.repository;

import dasturlash.uz.entity.Tag;
import dasturlash.uz.entity.VideoTag;
import dasturlash.uz.entity.video.Video;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VideoTagRepository extends JpaRepository<VideoTag, String> {

    // Soft delete: Mark tags as invisible instead of deleting rows
    @Modifying
    @Transactional
    @Query("UPDATE VideoTag vt SET vt.visible = FALSE WHERE vt.video = :video AND vt.tag IN :tags")
    void deleteByVideoAndTagIn(@Param("video") Video video, @Param("tags") List<Tag> tags);

    // Retrieve active (visible) tags for a video
    @Query("SELECT vt FROM VideoTag vt WHERE vt.video = :video ")
    List<VideoTag> findTagsByVideo(@Param("video") Video video);

    // Soft delete: Mark all tags for a video as invisible instead of deleting rows
    @Modifying
    @Transactional
    @Query("UPDATE VideoTag vt SET vt.visible = FALSE WHERE vt.video = :video")
    void deleteByVideo(@Param("video") Video video);

    // Check if the video-tag relationship exists and is soft-deleted (visible = false)
    @Query("SELECT vt FROM VideoTag vt WHERE vt.video.id = :videoId AND vt.tag.id = :tagId AND vt.visible = FALSE")
    Optional<VideoTag> findSoftDeletedVideoTag(@Param("videoId") String videoId, @Param("tagId") String tagId);


    @Query("SELECT vt.tag.name FROM VideoTag vt WHERE vt.video = :video AND vt.visible = true")
    List<String> findVisibleTagNamesByVideo(@Param("video") Video video);

    // Retrieve all video tags for a video, including soft-deleted ones
    @Query("SELECT vt FROM VideoTag vt WHERE vt.video = :video")
    List<VideoTag> findAllVideoTagsByVideo(@Param("video") Video video);

}


