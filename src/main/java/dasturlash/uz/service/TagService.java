package dasturlash.uz.service;

import dasturlash.uz.entity.Category;
import dasturlash.uz.entity.Tag;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.DataExistsException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String tagName) {
        // Normalize for searching (lowercase)
        String normalizedTagName = tagName.trim().toLowerCase();

        // Try to find existing tag first
        Optional<Tag> existingTag = tagRepository.findByNormalizedName(normalizedTagName);

        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        // When creating, preserve original casing
        Tag newTag = new Tag();
        newTag.setName(tagName.trim()); // Keep original casing
        newTag.setNormalizedName(normalizedTagName); // Add a normalized field for searching
        newTag.setVisible(Boolean.TRUE);
        newTag.setCreatedDate(LocalDateTime.now());

        return tagRepository.save(newTag);
    }

    // Method to handle multiple tag names
    public List<Tag> findOrCreateTags(List<String> tagNames) {
        // Validate tag names if needed
        validateTagNames(tagNames);

        return tagNames.stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toList());
    }

    private void validateTagNames(List<String> tagNames) {
        // Add custom validation logic
        for (String tagName : tagNames) {
            // Example validations
            if (tagName.length() > 50) {
                throw new AppBadRequestException("Tag name too long: " + tagName);
            }
            if (!tagName.matches("^[a-zA-Z0-9-]+$")) {
                throw new AppBadRequestException("Invalid tag name: " + tagName);
            }
        }
    }

    public PageImpl<Tag> getTagsList(Integer page, Integer size) {
        // Convert page to zero-indexed


        // Create pageable object with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Fetch only visible tags
        Page<Tag> tagPage = tagRepository.findByVisibleTrue(pageable);

        // Convert to PageImpl
        return new PageImpl<>(tagPage.getContent(), pageable, tagPage.getTotalElements());
    }

    public Tag updateById(String id, String newTag) {
        // Find existing tag
        Tag existingTag = tagRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Tag not found with id: " + id));

        // Check if new name already exists (if name is being changed)
        if (newTag != null && !existingTag.getName().equals(newTag)) {
            existsName(newTag);
            existingTag.setName(newTag);
        }

        // Update other fields if needed
        existingTag.setUpdatedDate(LocalDateTime.now());

        return tagRepository.save(existingTag);
    }

    public Boolean deleteById(String id) {
        Tag tag = tagRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Tag not found with id: " + id));

        // Soft delete
        tag.setVisible(Boolean.FALSE);
        tagRepository.save(tag);

        return true;
    }

    public void existsName(String name) {
        boolean isExist = tagRepository.existsByNameAndVisibleTrue(name);

        if (isExist) {
            throw new DataExistsException("Tag with name: " + name + " already exists");
        }
    }

}
