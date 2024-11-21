package dasturlash.uz.controller;

import dasturlash.uz.dto.request.CategoryRequestDTO;
import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    // Add new Category
    @PostMapping({"", "/"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CategoryRequestDTO creationDTO) {

        return ResponseEntity.status(201).body(categoryService.create(creationDTO));
    }

    // Update Category by id
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateById(@PathVariable("id") Long id,
                                        @RequestBody @Valid CategoryRequestDTO requestDTO) {

        return ResponseEntity.ok().body(categoryService.updateById(id, requestDTO));
    }
}
