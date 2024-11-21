package dasturlash.uz.repository;

import dasturlash.uz.entity.Attach;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttachRepository extends CrudRepository<Attach, String>, PagingAndSortingRepository<Attach, String> {

    @Transactional
    @Modifying
    @Query("UPDATE Attach a SET a.visible = ?2 WHERE a.id = ?1")
    void changeVisible(String id, Boolean aFalse);
}
