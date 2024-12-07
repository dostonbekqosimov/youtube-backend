package dasturlash.uz.service;

import dasturlash.uz.dto.PlaylistVideoDTO;
import dasturlash.uz.entity.video.PlaylistVideo;
import dasturlash.uz.repository.PlaylistVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistVideoService {

    private final PlaylistVideoRepository repository;

    public void create(PlaylistVideoDTO dto){
        boolean b = repository.existsByPlaylistIdAndVideoId(dto.getPlaylistId(), dto.getVideoId());
        if(b){
            throw new RuntimeException("There's already a video like this in the playlist");
        }
        PlaylistVideo video = new PlaylistVideo();
        video.setPlaylistId(dto.getPlaylistId());
        video.setVideoId(dto.getVideoId());
        video.setOrderNumber(dto.getOrderNumber());
        video.setCreatedDate(LocalDateTime.now());
        repository.save(video);
    }

    public int deleteSelectedPlaylist(String videoId, List<String> playlistIds){
        return repository.deleteByPlaylistIdAndVideoId(videoId, playlistIds);
    }

}
