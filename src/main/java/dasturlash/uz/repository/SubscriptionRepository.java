package dasturlash.uz.repository;

import dasturlash.uz.entity.Subscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, String> {

    @Transactional
    @Modifying
    @Query("delete from Subscription s where s.profileId = ?1 and s.channelId = ?2")
    int deleteByProfileIdAndChannelId(Long profileId, String channelId);

    Subscription findByProfileIdAndChannelId(Long profileId, String channelId);
}
