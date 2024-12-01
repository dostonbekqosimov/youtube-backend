package dasturlash.uz.controller;

import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<String> createComment(@RequestBody CommentCreateDTO request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

}
