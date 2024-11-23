package dasturlash.uz.controller;

import dasturlash.uz.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/video")
public class VideoController {

    private final VideoService videoService;
}
