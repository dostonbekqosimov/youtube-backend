package dasturlash.uz.controller;

import dasturlash.uz.entity.Tag;
import dasturlash.uz.service.CategoryService;
import dasturlash.uz.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    // Add new Category
    @PostMapping({"", "/"})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Tag> create(@RequestBody @Valid Tag creationDTO) {

        return ResponseEntity.status(201).body(tagService.create(creationDTO));
    }

    // Update Category by id
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Tag> updateById(@PathVariable("id") Long id,
                                                          @RequestBody @Valid Tag requestDTO) {

        return ResponseEntity.ok().body(tagService.updateById(id, requestDTO));
    }

    // Delete Category by id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") Long id) {
        Boolean result = tagService.deleteById(id);
        return ResponseEntity.ok(result);
    }

    // Get the list of Categories with pagination
    @GetMapping({"", "/"})
    public ResponseEntity<PageImpl<Tag>> getAll(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) {

        return ResponseEntity.ok().body(tagService.getCategoriesList(page - 1, size));
    }
}
