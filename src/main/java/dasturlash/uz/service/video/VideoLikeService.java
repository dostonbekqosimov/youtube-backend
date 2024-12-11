package dasturlash.uz.service.video;

import dasturlash.uz.dto.request.video.VideoLikedDTO;
import dasturlash.uz.dto.response.video.like.LikedAttachInfo;
import dasturlash.uz.dto.response.video.like.LikedVideoChannelInfo;
import dasturlash.uz.dto.response.video.like.LikedVideoInfo;
import dasturlash.uz.dto.response.video.like.VideoLikeInfo;
import dasturlash.uz.entity.video.VideoLike;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.LikeType;
import dasturlash.uz.mapper.GetUserLikedVideoInfoMapper;
import dasturlash.uz.repository.VideoLikeRepository;
import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoLikeService {
    private final VideoLikeRepository repository;
    private final AttachService attachService;

    private static Logger log = LoggerFactory.getLogger(VideoWatchedService.class);

    private Long currentUserId() {
        return getCurrentUserId();
    }


    public String create(VideoLikedDTO dto, LanguageEnum lang) {
        String likeTypeString = checkLike(currentUserId(), dto.getVideoId(), dto.getType());
        LikeType likeType = null;

        if (likeTypeString != null && likeTypeString != "NO") {
            likeType = LikeType.valueOf(likeTypeString);
        }
        if (likeType == null) {
            return "User cannot like and dislike at the same time";
        }
        log.info("Creating video like: {}", dto);
        VideoLike entity = new VideoLike();
        entity.setProfileId(currentUserId());
        entity.setVideoId(dto.getVideoId());
        entity.setType(likeType);
        entity.setCreatedDate(LocalDateTime.now());
        repository.save(entity);
        return likeTypeString;
    }


    public String checkLike(Long profileId, String videoId, LikeType type) {
        VideoLike videoLike = repository.findByProfileIdAndVideoId(profileId, videoId);
        if (videoLike != null) {
            if (videoLike.getType().equals(type)){
                repository.delete(videoLike);
                log.info("Deleted video like: {}", videoLike);
                return "NO";
            }else {
                repository.delete(videoLike);
                return type.toString();
            }

        }
        log.info("Removing video like: {}", type);
        return type.toString();
    }


    public List<VideoLikeInfo> getUserLikedVideos(String lang) {
        List<GetUserLikedVideoInfoMapper> userLikedVideoInfoMapper = repository.getUserLikedVideoInfoMapper(currentUserId());
        return toUserLikedVideos(userLikedVideoInfoMapper);
    }

    public List<VideoLikeInfo> getUserLikedVideosAdmin(String lang, Long userId) {
        List<GetUserLikedVideoInfoMapper> userLikedVideoInfoMapper = repository.getUserLikedVideoInfoMapper(userId);
        return toUserLikedVideos(userLikedVideoInfoMapper);
    }

    //Bu methodni tushun uchun uni return type da classni va mapperni yaxshilab organib chiqing
    public List<VideoLikeInfo> toUserLikedVideos(List<GetUserLikedVideoInfoMapper> mappers) {
            List<VideoLikeInfo> userLikedVideos = new ArrayList<>();
        VideoLikeInfo videoLikeInfo = new VideoLikeInfo();
        LikedVideoInfo likedVideo = new LikedVideoInfo();
        LikedVideoChannelInfo channelInfo = new LikedVideoChannelInfo();
        LikedAttachInfo likedVideoAttaches = new LikedAttachInfo();
        for (GetUserLikedVideoInfoMapper mapperData : mappers) {
            //Basic response DTO qiymatlari set qilinayapti
            videoLikeInfo.setVideoLikeId(mapperData.getVideoLikedId());

            //Basic DTO ichidagi field class qiymatlari set qilinayapti: Basic DTO classni  1-field Class
            likedVideo.setVideoId(mapperData.getVideoId());
            likedVideo.setName(mapperData.getVideoName());
            likedVideo.setDuration(attachService.getDurationFromEntity(mapperData.getAttachId()));

            //1-field class ichidagi field classni qiymatlari set qilinayapti: 1-field DTO classni 1-field DTO classi
            channelInfo.setChannelId(mapperData.getChannelId());
            channelInfo.setName(mapperData.getChannelName());

            //Basic DTO ichidagi field class qiymatlari set qilinayapti: Basic DTO classni 2- field Class
            likedVideoAttaches.setAttachId(mapperData.getAttachId());
            likedVideoAttaches.setUrl(attachService.openURL(mapperData.getAttachId()));

            //Basic DTO classni qolgan field class lari set qilinayapti, qolgan classlarga qaram bolgani uchun
            likedVideo.setChannel(channelInfo);
            videoLikeInfo.setVideo(likedVideo);
            videoLikeInfo.setPreview(likedVideoAttaches);
            userLikedVideos.add(videoLikeInfo);
        }
        return userLikedVideos;
    }


}
