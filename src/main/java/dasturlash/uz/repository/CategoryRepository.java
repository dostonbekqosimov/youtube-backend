package dasturlash.uz.repository;

import dasturlash.uz.entity.Category;
import dasturlash.uz.mapper.CategoryShortInfoMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long>, PagingAndSortingRepository<Category, Long> {


    boolean existsByName(String name);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.visible = ?2 WHERE c.id = ?1")
    Integer changeVisible(Long id, Boolean visible);

    Optional<Category> findByIdAndVisibleTrue(Long id);

    boolean existsByNameAndVisibleTrue(String name);

    @Query("SELECT c FROM Category c WHERE c.visible = true")
    Page<Category> findAllAndVisibleTrue(Pageable pageable);

    @Query("select c.id as id, c.name as name from Category c where c.id = ?1")
    CategoryShortInfoMapper toShortInfoMapper(Long categoryId);
}
