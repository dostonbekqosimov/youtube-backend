package dasturlash.uz.service;

import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.dto.request.comment.CommentUpdateDTO;
import dasturlash.uz.entity.Comment;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public String createComment(CommentCreateDTO request) {

        Comment newComment = new Comment();
        newComment.setContent(request.getContent());
        newComment.setProfileId(getCurrentUserId());
        newComment.setVideoId(request.getVideoId());
        newComment.setReplyId(request.getReplyId());


        newComment.setLikeCount(0);
        newComment.setDislikeCount(0);
        newComment.setVisible(Boolean.TRUE);
        newComment.setCreatedDate(LocalDateTime.now());

        commentRepository.save(newComment);

        return "New comment created with id: " + newComment.getId();
    }


    public String updateComment(CommentUpdateDTO request) {

        Comment existingComment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));

        // Validate user permission
        if (!existingComment.getProfileId().equals(getCurrentUserId())) {
            throw new ForbiddenException("You are not authorized to update this comment");
        }

        existingComment.setContent(request.getContent());
        existingComment.setUpdatedDate(LocalDateTime.now());

        commentRepository.save(existingComment);

        return "Comment updated successfully with id: " + request.getId();
    }
}
