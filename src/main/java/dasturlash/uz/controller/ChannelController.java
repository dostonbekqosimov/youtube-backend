package dasturlash.uz.controller;

import dasturlash.uz.dto.request.ChannelCreateRequest;
import dasturlash.uz.dto.response.ChannelResponseDTO;
import dasturlash.uz.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // version 0.0.1 of creating channel. Now I only create channels with name, description and handle(unique name for channel)
    // in the future I will include adding photo.
    @PostMapping({"", "/"})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> create(@RequestBody @Valid ChannelCreateRequest request) {
        channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/channel")
    public ResponseEntity<ChannelResponseDTO> getChannelById(@RequestParam("id") String channelId) {

        return ResponseEntity.ok().body(channelService.getChannelById(channelId));

    }
}
