package dasturlash.uz.service.video;

import dasturlash.uz.repository.VideoViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoViewService {

    private final VideoViewRepository videoViewRepository;
}
