package dasturlash.uz.repository;

import dasturlash.uz.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findAllByProfileId(Long profileId);

    Page<Comment> findAllByVideoId(Pageable pageable, String videoId);
}
