package dasturlash.uz.service;

import dasturlash.uz.dto.request.playlist.ChangeStatusDTO;
import dasturlash.uz.dto.request.playlist.PlaylistDTO;
import dasturlash.uz.dto.response.channel.ChannelResponseDTO;
import dasturlash.uz.entity.video.Playlist;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.repository.PlaylistRepository;
import dasturlash.uz.security.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final ChannelService channelService;
    private static final Logger log = LoggerFactory.getLogger(PlaylistService.class);

    public PlaylistDTO create(PlaylistDTO dto) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        log.debug("Create playlist with name: {}", dto.getName());

        ChannelResponseDTO channelById = channelService.getChannelById(dto.getChannelId());
        if (channelById == null) {
            log.error("Channel not found for channelId: {}", dto.getChannelId());
            throw new AppBadRequestException("Channel not found");
        }

        if (!channelById.getProfileId().equals(currentUserId)) {
            log.error("User {} does not have permission to create playlist for channelId: {}", currentUserId, dto.getChannelId());
            throw new AppBadRequestException("You do not have permission to create playlist for this channel");
        }

        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setStatus(dto.getStatus());
        playlist.setChannelId(dto.getChannelId());
        playlist.setOrderNumber(dto.getOrderNumber());
        playlist.setCreatedDate(LocalDateTime.now());
        playlist.setVisible(true);

        playlistRepository.save(playlist);
        log.info("Playlist created with name: {}", playlist.getName());
        return dto;
    }


    public PlaylistDTO update(PlaylistDTO dto, String playlistId) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);

        log.debug("Update playlist with name: {}", dto.getName());

        Long channelOwner = playlistRepository.checkOwner(playlistId);
        if (!Objects.equals(currentUserId, channelOwner)) {
            log.error("User {} does not have permission to update playlist with name: {}", currentUserId, dto.getName());
            throw new AppBadRequestException("You do not have permission to update playlist");
        }

        if (!optionalPlaylist.isPresent()) {
            log.error("Playlist not found for playlistId: {}", playlistId);
            throw new AppBadRequestException("Playlist not found");
        }
        Playlist playlist = optionalPlaylist.get();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setStatus(dto.getStatus());
        playlist.setOrderNumber(dto.getOrderNumber());
        playlistRepository.save(playlist);
        log.info("Playlist updated with name: {}", playlist.getName());
        dto.setChannelId(playlist.getChannelId());
        return dto;
    }


    public String changeStatus(ChangeStatusDTO status) {
        Long currentUserId = SpringSecurityUtil.getCurrentUserId();
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(status.getPlaylistId());


        Long channelOwner = playlistRepository.checkOwner(status.getPlaylistId());
        if (!Objects.equals(currentUserId, channelOwner)) {
            log.error("User {} does not have permission to change playlist status: {}", currentUserId, status.getPlaylistId());
            throw new AppBadRequestException("You do not have permission to change playlist status");
        }

        if (!optionalPlaylist.isPresent()) {
            log.error("Playlist not found for playlistId: {}", status.getPlaylistId());
            throw new AppBadRequestException("Playlist not found");
        }

        Playlist playlist = optionalPlaylist.get();
        playlist.setStatus(status.getStatus());
        playlistRepository.save(playlist);
        log.info("Playlist updated with name: {}", playlist.getName());
        return "Changed status to " + status.getStatus();
    }

    public String delete(String playlistId) {
        Long ownerPlaylist = playlistRepository.checkOwner(playlistId);
        if (!Objects.equals(ownerPlaylist, SpringSecurityUtil.getCurrentUserId())) {
            log.error("User {} does not have permission to delete playlist: {}", ownerPlaylist, playlistId);
            throw new AppBadRequestException("You do not have permission to delete playlist");
        }

        if (!SpringSecurityUtil.getCurrentUserRole().equals(ProfileRole.ROLE_ADMIN)){
            log.error("User {} does not have permission to delete playlist: {}", SpringSecurityUtil.getCurrentUserId(), playlistId);
            throw new AppBadRequestException("You do not have permission to delete playlist");
        }
        playlistRepository.setVisibleFalse(playlistId);
        log.info("Playlist deleted with name: {}", playlistId);
        return "Deleted playlist";
    }
}
