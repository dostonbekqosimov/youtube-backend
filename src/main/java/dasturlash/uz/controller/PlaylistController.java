package dasturlash.uz.controller;

import dasturlash.uz.dto.request.playlist.ChangeStatusDTO;
import dasturlash.uz.dto.request.playlist.PlaylistDTO;
import dasturlash.uz.dto.response.playlist.PlayListShortInfoAdmin;
import dasturlash.uz.dto.response.playlist.PlayListShortInfoUser;
import dasturlash.uz.repository.PlaylistRepository;
import dasturlash.uz.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class   PlaylistController {
    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class);
    private final PlaylistService service;


    @PostMapping("/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO dto) {
        log.info("Creating playlist");
        log.debug("dto: {}", dto);

        try{
            PlaylistDTO response = service.create(dto);
            log.info("Playlist create success with name: {}", response.getName());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error("Error during creating playlist", e);
            throw e;
        }
    }

    @PutMapping("/update/{playlistId}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@RequestBody PlaylistDTO dto, @PathVariable String playlistId) {
        log.info("Updating playlist");
        log.debug("dto: {}", dto);

        try {
            PlaylistDTO update = service.update(dto, playlistId);
            log.info("Playlist update success with name: {}", update.getName());
            return ResponseEntity.ok(update);
        }catch (Exception e){
            log.error("Error during updating playlist", e);
            throw e;
        }
    }

    @PutMapping("/change-status")
    public ResponseEntity<String> changePlaylistStatus(@RequestBody ChangeStatusDTO status) {
        log.info("Changing playlist status");
        log.debug("status: {}", status);

        try {
            log.info("Playlist change status success with name");
            return ResponseEntity.ok(service.changeStatus(status));
        }catch (Exception e){
            log.error("Error during changing playlist status", e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{playlistId}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String playlistId) {
        log.info("Deleting playlist");
        log.debug("playlist: {}", playlistId);

        try {
            log.info("Playlist delete success with name");
            return ResponseEntity.ok(service.delete(playlistId));
        }catch (Exception e){
            log.error("Error during deleting playlist", e);
            throw e;
        }
    }

    @GetMapping("/get-pagination-admin")
    public ResponseEntity<Page<PlayListShortInfoAdmin>> getPlaylistsPagination(@RequestParam(defaultValue = "0")int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Getting playlists pagination");
        log.debug("page: {}", page);
        try {
            log.info("Playlists pagination success with name: {}", page);
            Page<PlayListShortInfoAdmin> playlist = service.getPagination(page, size);
            return ResponseEntity.ok(playlist);
        }catch (Exception e){
            log.error("Error during getting playlists", e);
            throw e;
        }

    }

    @GetMapping("/get-pagenation-user")
    public ResponseEntity<Page<PlayListShortInfoUser>> getPlaylistPaginationUser(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("Getting playlists pagination user");
        log.debug("page: {}", page);
        try {
            log.info("Playlists pagination user success with name: {}", page);
            Page<PlayListShortInfoUser> pagePlaylist = service.getPaginationUser(page, size);
            return ResponseEntity.ok(pagePlaylist);
        }catch (Exception e){
            log.error("Error during getting playlists", e);
            throw e;
        }
    }
}
