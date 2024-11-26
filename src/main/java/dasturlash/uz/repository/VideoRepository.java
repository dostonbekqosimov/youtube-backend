package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.mapper.AdminVideoProjection;
import dasturlash.uz.mapper.VideoShortInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


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
            "WHERE v.categoryId = :categoryId AND v.status = 'PUBLIC' AND v.visible = true")
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
            "WHERE lower(v.title) like lower(concat('%', :title, '%')) AND v.status = 'PUBLIC' AND v.visible = true")
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
            "WHERE v.channelId = :channelId AND v.status = 'PUBLIC' AND v.visible = true")
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


}
