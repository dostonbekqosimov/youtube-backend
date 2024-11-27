package dasturlash.uz.service;

import dasturlash.uz.entity.Category;
import dasturlash.uz.entity.Tag;
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
        // Try to find existing tag first
        Optional<Tag> existingTag = tagRepository.findByNameAndVisibleTrue(tagName);

        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        // If tag doesn't exist, create a new one
        Tag newTag = new Tag();
        newTag.setName(tagName.toLowerCase());
        newTag.setVisible(Boolean.TRUE);
        newTag.setCreatedDate(LocalDateTime.now());

        return tagRepository.save(newTag);
    }

    // Method to handle multiple tag names
    public List<Tag> findOrCreateTags(List<String> tagNames) {
        return tagNames.stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toList());
    }

    public PageImpl<Tag> getTagsList(String page, Integer size) {
        // Convert page to zero-indexed
        int pageNum = Integer.parseInt(page) - 1;

        // Create pageable object with sorting
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("createdDate").descending());

        // Fetch only visible tags
        Page<Tag> tagPage = tagRepository.findByVisibleTrue(pageable);

        // Convert to PageImpl
        return new PageImpl<>(tagPage.getContent(), pageable, tagPage.getTotalElements());
    }

    public Tag updateById(String id, Tag requestDTO) {
        // Find existing tag
        Tag existingTag = tagRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new DataNotFoundException("Tag not found with id: " + id));

        // Check if new name already exists (if name is being changed)
        if (requestDTO.getName() != null && !existingTag.getName().equals(requestDTO.getName())) {
            existsName(requestDTO.getName());
            existingTag.setName(requestDTO.getName());
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
