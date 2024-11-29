package dasturlash.uz.repository;

import dasturlash.uz.entity.video.VideoShareRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoShareRepository extends JpaRepository<VideoShareRecord, String> {

}
