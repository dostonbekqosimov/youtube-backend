package dasturlash.uz.service;

import dasturlash.uz.dto.request.CategoryRequestDTO;
import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.entity.Category;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO create(@Valid CategoryRequestDTO creationDTO) {

        // check if the category type exists
        existsName(creationDTO.getName());

        Category newCategory = new Category();
        newCategory.setName(creationDTO.getName());
        newCategory.setCreatedDate(LocalDateTime.now());

        categoryRepository.save(newCategory);

        return toDto(newCategory);
    }

    public void existsName(String name) {
        boolean isExist = categoryRepository.existsByName(name);

        if (isExist) {
            throw new DataExistsException("Category with name: " + name + " exists");
        }
    }

    private CategoryResponseDTO toDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCreatedDate(category.getCreatedDate());
        return dto;
    }
}
