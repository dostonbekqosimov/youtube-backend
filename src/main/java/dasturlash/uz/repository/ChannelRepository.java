package dasturlash.uz.repository;

import dasturlash.uz.entity.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends CrudRepository<Channel, String> {
    boolean existsByHandle(String handle);

    @Query("select c from Channel c where lower(c.name) like lower(concat('%', :name, '%')) and c.visible = true")
    List<Channel> findByName(@Param("name") String name);

    @Query("select c from Channel c where lower(c.handle) like lower(concat('%', :handle, '%')) and c.visible = true")
    List<Channel> findByHandle(@Param("handle")String channelHandle);

    Page<Channel> findAllByVisibleTrue(Pageable pageable);
}
