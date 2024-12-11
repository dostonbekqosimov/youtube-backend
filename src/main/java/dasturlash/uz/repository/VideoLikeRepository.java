package dasturlash.uz.repository;

import dasturlash.uz.entity.video.VideoLike;
import dasturlash.uz.mapper.GetUserLikedVideoInfoMapper;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface VideoLikeRepository extends CrudRepository<VideoLike, String> {

    VideoLike findByProfileIdAndVideoId(Long profileId, String videoId);


   @Query("select vl.id as videoLikedId, " +
           "v.id as videoId," +
           "v.title as videoName," +
           "c.id as channelId," +
           "c.name as channelName," +
           "a.id as attachId " +
           "from VideoLike vl " +
           "join vl.video v " +
           "join v.channel c " +
           "join v.preview a " +
           "where vl.profileId = ?1")
    List<GetUserLikedVideoInfoMapper> getUserLikedVideoInfoMapper(Long profileId);

}
