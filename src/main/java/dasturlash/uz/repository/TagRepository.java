package dasturlash.uz.repository;

import dasturlash.uz.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, String> {

    Optional<Tag> findByNameIgnoreCase(String normalizedName);
    Optional<Tag> findByNormalizedName(String normalizedName);
    Optional<Tag> findByNameAndVisibleTrue(String name);
    Optional<Tag> findByIdAndVisibleTrue(String id);
    boolean existsByNameAndVisibleTrue(String name);
    Page<Tag> findByVisibleTrue(Pageable pageable);
}
