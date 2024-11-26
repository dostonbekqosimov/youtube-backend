package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Video;
import dasturlash.uz.enums.ContentStatus;
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
            "v.viewCount AS viewCount, a.duration AS duration " +
            "FROM Video v " +
            "LEFT JOIN v.preview p " +
            "LEFT JOIN v.channel c " +
            "LEFT JOIN c.photo cp " +
            "LEFT JOIN v.video a " +
            "WHERE v.categoryId = :categoryId AND v.status = :status AND v.visible = true")
    Page<VideoShortInfoProjection> findShortVideoInfoByCategoryId(@Param("categoryId") Long categoryId, @Param("status") ContentStatus status, Pageable pageable);

}
