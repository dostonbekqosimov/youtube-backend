package dasturlash.uz.repository;

import dasturlash.uz.entity.Subscription;
import dasturlash.uz.mapper.GetSubscriptionChannelsInfoMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, String> {

    @Transactional
    @Modifying
    @Query("delete from Subscription s where s.profileId = ?1 and s.channelId = ?2")
    int deleteByProfileIdAndChannelId(Long profileId, String channelId);

    Subscription findByProfileIdAndChannelId(Long profileId, String channelId);

    @Transactional
    @Modifying
    @Query("update Subscription s " +
            "set s.status = ?1 " +
            "where s.profileId = ?2 and s.channelId = ?3")
    int updateStatus(String status, Long profileId, String channelId);

    @Transactional
    @Modifying
    @Query("update Subscription s " +
            "set s.notificationType = ?1 " +
            "where s.profileId = ?2 and s.channelId = ?3")
    int updateNotificationStatus(String status, Long profileId, String channelId);

    @Query("select s.id as subsId, " +
            "c.id as channelId, " +
            "c.name as channelName, " +
            "c.photoId as channelPhoto, " +
            "s.notificationType as notificationType " +
            "from Subscription s " +
            "join s.channel c " +
            "where s.profileId = ?1")
    List<GetSubscriptionChannelsInfoMapper> getUserSubscribedChannels(Long userId);
}
