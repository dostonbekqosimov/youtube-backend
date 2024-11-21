package dasturlash.uz.repository;

import dasturlash.uz.entity.Attach;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttachRepository extends CrudRepository<Attach, String>, PagingAndSortingRepository<Attach, String> {
}
