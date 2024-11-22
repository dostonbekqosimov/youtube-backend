package dasturlash.uz.controller;

import dasturlash.uz.dto.request.ChannelMediaUpdateRequest;
import dasturlash.uz.dto.request.ChannelCreateRequest;
import dasturlash.uz.dto.response.ChannelResponseDTO;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.service.ChannelService;
import dasturlash.uz.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // version 0.0.1 of creating channel. Now I only create channels with name, description and handle(unique name for channel)
    // in the future I will include adding photo.
    @PostMapping({"", "/"})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> create(@RequestBody @Valid ChannelCreateRequest request,
                                       @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader) {
        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);

        channelService.create(request, lang);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Update channel info(name, description, handle) (User and Owner)
    @PatchMapping("/edit-info")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateChannelInfo(@RequestParam("id") String channelId,
                                                  @RequestBody ChannelCreateRequest updateRequest,
                                                  @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader) {

        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        channelService.updateChannelInfo(channelId, updateRequest, lang);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Update channel photo
    @PatchMapping("/edit-photo")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateChannelPhoto(@RequestParam("id") String channelId,
                                                   @RequestBody ChannelMediaUpdateRequest updateRequest,
                                                   @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader) {

        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        channelService.updateChannelPhoto(channelId, updateRequest, lang);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Update channel banner
    @PatchMapping("/edit-photo")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateChannelBanner(@RequestParam("id") String channelId,
                                                    @RequestBody ChannelMediaUpdateRequest updateRequest,
                                                    @RequestHeader(value = "Accept-Language", defaultValue = "uz") String languageHeader) {

        LanguageEnum lang = LanguageUtil.getLanguageFromHeader(languageHeader);
        channelService.updateChannelBanner(channelId, updateRequest, lang);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Get Channel by ID
    @GetMapping("/id")
    public ResponseEntity<ChannelResponseDTO> getChannelById(@RequestParam("id") String channelId) {
        return ResponseEntity.ok().body(channelService.getChannelById(channelId));
    }

    // Get Channels by Name
    @GetMapping("/name")
    public ResponseEntity<List<ChannelResponseDTO>> getChannelListByName(@RequestParam("name") String channelName) {
        return ResponseEntity.ok().body(channelService.getChannelByName(channelName));
    }

    // Get Channels by Handle
    @GetMapping("/handle")
    public ResponseEntity<List<ChannelResponseDTO>> getChannelListByHandle(@RequestParam("handle") String channelHandle) {
        return ResponseEntity.ok().body(channelService.getChannelByHandle(channelHandle));
    }
}
