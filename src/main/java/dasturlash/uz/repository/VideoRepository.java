package dasturlash.uz.repository;

import dasturlash.uz.entity.video.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VideoRepository extends CrudRepository<Video, String>, PagingAndSortingRepository<Video, String> {
}
