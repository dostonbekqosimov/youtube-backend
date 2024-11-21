package dasturlash.uz.repository;

import dasturlash.uz.entity.Channel;
import org.springframework.data.repository.CrudRepository;

public interface ChannelRepository extends CrudRepository<Channel, String> {
    boolean existsByHandle(String handle);
}
