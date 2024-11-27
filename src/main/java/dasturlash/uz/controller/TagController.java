package dasturlash.uz.controller;

import dasturlash.uz.entity.Tag;
import dasturlash.uz.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    // Add new Tag
    @PostMapping({"", "/"})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Tag> create(@RequestBody String tagName) {
        return ResponseEntity.status(201).body(tagService.findOrCreateTag(tagName));
    }

    // Update Tag by id
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Tag> updateById(@PathVariable("id") String id,
                                          @RequestBody Tag request) {
        return ResponseEntity.ok().body(tagService.updateById(id, request));
    }

    // Delete Tag by id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") String id) {
        return ResponseEntity.ok(tagService.deleteById(id));
    }

    // Get the list of Tags with pagination
    @GetMapping({"", "/"})
    public ResponseEntity<PageImpl<Tag>> getAll(
            @RequestParam(value = "page", defaultValue = "1") String page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return ResponseEntity.ok().body(tagService.getTagsList(page, size));
    }
}