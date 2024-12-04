package dasturlash.uz.controller;

import dasturlash.uz.service.PlaylistVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlistVideo")
@RequiredArgsConstructor
public class PlaylistVideoController {
    private final PlaylistVideoService service;
}
