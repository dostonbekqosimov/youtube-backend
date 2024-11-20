package dasturlash.uz.service;

import dasturlash.uz.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttachService {

    private final AttachRepository attachRepository;
}
