package dasturlash.uz.controller;


import dasturlash.uz.dto.response.video.VideoFullInfoDTO;
import dasturlash.uz.dto.response.video.VideoHistoryDTO;
import dasturlash.uz.service.video.VideoWatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/videoWatched")
@RequiredArgsConstructor
public class VideoWatchedController {
    private final VideoWatchedService service;

    @GetMapping("/get-history")
    public ResponseEntity<List<VideoHistoryDTO>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }
}
