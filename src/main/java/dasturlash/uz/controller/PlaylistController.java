package dasturlash.uz.controller;

import dasturlash.uz.dto.request.PlaylistDTO;
import dasturlash.uz.entity.video.Playlist;
import dasturlash.uz.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {
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
}
