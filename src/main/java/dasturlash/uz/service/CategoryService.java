package dasturlash.uz.service;

import dasturlash.uz.dto.request.CategoryRequestDTO;
import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.entity.Category;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.mapper.CategoryShortInfoMapper;
import dasturlash.uz.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO create(@Valid CategoryRequestDTO creationDTO) {

        // check if the category type exists
        existsName(creationDTO.getName());

        Category newCategory = new Category();
        newCategory.setName(creationDTO.getName());
        newCategory.setVisible(Boolean.TRUE);
        newCategory.setCreatedDate(LocalDateTime.now());

        categoryRepository.save(newCategory);

        return toDto(newCategory);
    }

    public CategoryResponseDTO updateById(Long id, @Valid CategoryRequestDTO requestDTO) {

        // check if it exists
        // get category
        Category existingCategory = getById(id);

        // check if the category name exists
        existsName(requestDTO.getName());

        // update category name
        existingCategory.setName(requestDTO.getName());

        // set updated date
        existingCategory.setUpdatedDate(LocalDateTime.now());

        // save new category name in db
        categoryRepository.save(existingCategory);

        // map to dto and return
        return toDto(existingCategory);
    }

    public Boolean deleteById(Long id) {

        // check if it exists
        // get category
        getById(id);

        // change visible to false
        Integer result = categoryRepository.changeVisible(id, Boolean.FALSE);

        return result > 0;


    }

    public PageImpl<CategoryResponseDTO> getCategoriesList(int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        Page<Category> articleTypePage = categoryRepository.findAllAndVisibleTrue(pageRequest);

        if (articleTypePage.isEmpty()) {
            throw new DataNotFoundException("No category found");
        }

        // Convert to DTOs
        List<CategoryResponseDTO> responseDTOS = articleTypePage
                .stream()
                .map(this::toDto)
                .toList();
        // Create a new Page with the DTOs
        return new PageImpl<>(responseDTOS, pageRequest, articleTypePage.getTotalElements());
    }

    public void existsName(String name) {
        boolean isExist = categoryRepository.existsByNameAndVisibleTrue(name);

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

    public CategoryResponseDTO getCategoryShortInfoById(Long id) {
        CategoryShortInfoMapper mapper = categoryRepository.toShortInfoMapper(id);
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(mapper.getId());
        dto.setName(mapper.getName());
        return dto;
    }


    public Category getById(Long id) {
        return categoryRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Category with id: " + id + " not found"));
    }


}
