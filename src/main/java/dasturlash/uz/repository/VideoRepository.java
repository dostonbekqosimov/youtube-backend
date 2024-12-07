package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.mapper.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VideoRepository extends CrudRepository<Video, String>, PagingAndSortingRepository<Video, String> {

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "JOIN v.videoTags vt " +
            "JOIN vt.tag t " +
            "WHERE t.name = :tagName AND t.visible = true " +
            "ORDER BY v.publishedDate DESC")
    Page<VideoShortInfoProjection> findVideosByTagName(@Param("tagName") String tagName, Pageable pageable);


    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "WHERE v.categoryId = :categoryId AND v.status = 'PUBLIC' AND v.visible = true " +
            "order by v.publishedDate desc")
    Page<VideoShortInfoProjection> findPublicVideosByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "WHERE lower(v.title) like lower(concat('%', :title, '%')) AND v.status = 'PUBLIC' AND v.visible = true " +
            "order by v.publishedDate desc")
    Page<VideoShortInfoProjection> findPublicVideosByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "WHERE v.channelId = :channelId AND v.status = 'PUBLIC' AND v.visible = true " +
            "order by v.publishedDate desc")
    Page<VideoShortInfoProjection> findPublicChannelVideosListByChannelId(@Param("channelId") String channelId, Pageable pageable);

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "o.id AS ownerId, o.name AS ownerName, o.surname AS ownerSurname, " +
            "pl.id AS playlistId, pl.name AS playlistName, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.profile o " + // Assuming `Channel` has a relationship with the `Owner`
            "LEFT JOIN v.playlist pl " + // Assuming videos can have a playlist
            "LEFT JOIN v.video a " +
            "WHERE v.visible = true")
    Page<AdminVideoProjection> findAdminVideoInfo(Pageable pageable);

    @Query("select count(v.id) as videoCount, sum(v.viewCount)as totalViewCount from Video v where v.playlistId = ?1")
    VideoInfoInPlaylist findVideoInfoById(String playlistId);

    Optional<Video> findByIdAndVisibleTrue(String videoId);

    @Modifying
    @Transactional
    @Query("update Video set visible = :visible where id = :videoId")
    Integer changeVisibility(@Param("videoId") String videoId, @Param("visible") Boolean visible);

    @Query("SELECT " +
            "v.id AS id, " +
            "v.title AS title, " +
            "c.name AS channelName, " +
            "v.sharedCount AS sharedCount, " +
            "v.previewAttachId AS previewAttachId, " +
            "v.attachId AS attachId " +
            "FROM Video v " +
            "LEFT JOIN v.channel c " +
            "WHERE v.id = :videoId")
    Optional<VideoShareProjection> findVideoShareInfoById(@Param("videoId") String videoId);

    @Query("select " +
            "v.title as title, " +
            "c.name as channelName, " +
            "v.viewCount as viewCount, " +
            "v.attachId as attachId " +
            "from Video v " +
            "join v.channel c " +
            "where v.id = ?1")
    VideoWatchedHistory findVideoById(String videoId);

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "JOIN v.videoTags vt " +
            "JOIN vt.tag t " +
            "WHERE v.id = :videoId AND t.visible = true " +
            "ORDER BY v.publishedDate DESC")
    Page<VideoShortInfoProjection> findVideosByVideoId(@Param("videoId") String videoId, Pageable pageable);

    @Query("SELECT v.id AS id, v.title AS title, " +
            "p.id AS previewAttachId, " +
            "c.id AS channelId, " +
            "v.viewCount AS viewCount, a.duration AS duration, " +
            "v.publishedDate AS publishedDate " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "JOIN v.videoTags vt " +
            "JOIN vt.tag t " +
            "WHERE v.id IN :videoIds AND t.visible = true " +
            "ORDER BY v.publishedDate DESC")
    List<VideoShortInfoProjection> findVideosByVideoIds(@Param("videoIds") List<String> videoIds);

}
