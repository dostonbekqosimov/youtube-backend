package dasturlash.uz.controller;

import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

}
