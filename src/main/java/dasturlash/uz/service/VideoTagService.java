package dasturlash.uz.service;

import dasturlash.uz.repository.VideoTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoTagService {

    private final VideoTagRepository videoTagRepository;
}
