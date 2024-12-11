package dasturlash.uz.controller;

import dasturlash.uz.dto.request.video.VideoLikedDTO;
import dasturlash.uz.dto.response.video.like.VideoLikeInfo;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.video.VideoLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/video-like")
@RequiredArgsConstructor
public class VideoLikeController {
    private final VideoLikeService service;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody @Valid VideoLikedDTO dto,
                                         @RequestHeader(value = "Accept-Language", defaultValue = "uz")String lang){
        LanguageEnum languageEnum = LanguageEnum.valueOf(lang);
        return ResponseEntity.ok(service.create(dto, languageEnum));
    }

    @GetMapping("/get-user-liked-videos")
    public ResponseEntity<List<VideoLikeInfo>> getUserLikedVideos(@RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang){
        LanguageEnum languageEnum = LanguageEnum.valueOf(lang);
        return ResponseEntity.ok(service.getUserLikedVideos(lang));
    }

    @GetMapping("/get-user-liked-videos-Admin/{userId}")
    public ResponseEntity<List<VideoLikeInfo>> getUserLikedVideosAdmin(@RequestHeader(value = "Accept-Language", defaultValue = "uz") String lang,
                                                                       @PathVariable @Valid Long userId){
        LanguageEnum languageEnum = LanguageEnum.valueOf(lang);
        return ResponseEntity.ok(service.getUserLikedVideosAdmin(lang, userId));
    }
}
