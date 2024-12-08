package dasturlash.uz.controller;

import dasturlash.uz.dto.response.comment.AdminCommentInfoDTO;
import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.dto.request.comment.CommentUpdateDTO;
import dasturlash.uz.dto.response.comment.CommentInfoDTO;
import dasturlash.uz.service.CommentService;
import jakarta.validation.Valid;
import jdk.dynalink.linker.LinkerServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<String> createComment(@RequestBody @Valid CommentCreateDTO request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

    @PatchMapping("")
    public ResponseEntity<String> updateComment(
            @Valid @RequestBody CommentUpdateDTO request
    ) {
        return ResponseEntity.ok(commentService.updateComment(request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable String commentId) {

        return ResponseEntity.ok(commentService.deleteCommentById(commentId));
    }

    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<AdminCommentInfoDTO>> getCommentList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        return ResponseEntity.ok().body(commentService.getAllComments(page - 1, size));
    }

    @GetMapping("/admin/list/profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdminCommentInfoDTO>> getCommentListByProfileId(
            @RequestParam("profileId") Long profileId) {

        return ResponseEntity.ok().body(commentService.getCommentListByProfileId(profileId));

    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<PageImpl<CommentInfoDTO>> getCommentListByVideoId(
            @PathVariable("videoId") String videoId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {


        return ResponseEntity.ok(commentService.getCommentListByVideoId(page - 1, size, videoId));
    }
}
