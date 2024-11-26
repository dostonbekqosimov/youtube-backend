package dasturlash.uz.service;

import dasturlash.uz.entity.Tag;
import dasturlash.uz.repository.TagRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag create(Tag creationDTO) {
        return null;
    }

    public Tag updateById(Long id, Tag requestDTO) {
        return null;
    }

    public Boolean deleteById(Long id) {
        return true;
    }

    public PageImpl<Tag> getCategoriesList(int i, Integer size) {
        return null;
    }
}
