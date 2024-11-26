package dasturlash.uz.repository;

import dasturlash.uz.entity.Channel;
import dasturlash.uz.mapper.ChannelShortInfoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelRepository extends CrudRepository<Channel, String> {
    boolean existsByHandle(String handle);

    @Query("select c from Channel c where lower(c.name) like lower(concat('%', :name, '%')) and c.visible = true")
    List<Channel> findByName(@Param("name") String name);

    @Query("select c from Channel c where lower(c.handle) like lower(concat('%', :handle, '%')) and c.visible = true")
    List<Channel> findByHandleWithSearchForAll(@Param("handle")String channelHandle);

    Page<Channel> findAllByVisibleTrue(Pageable pageable);

    Page<Channel> findAllByProfileIdAndVisibleTrue(Long currentUserId, Pageable pageRequest);

    Channel findByHandle(String handle);

    @Query("select c.id as id, c.name as name, c.photoId as photoId from Channel c where c.id = ?1")
    ChannelShortInfoMapper getShortInfo(String channelId);

    @Query("select c.id from Channel c where c.profileId = ?1")
    String findChannelIdByProfileId(Long profileId);
}
