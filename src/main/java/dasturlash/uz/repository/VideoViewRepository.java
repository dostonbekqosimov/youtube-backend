package dasturlash.uz.repository;

import dasturlash.uz.entity.video.VideoViewRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoViewRepository extends JpaRepository<VideoViewRecord, String> {
}
