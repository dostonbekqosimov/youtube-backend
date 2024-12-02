package dasturlash.uz.service;

import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.dto.request.comment.CommentUpdateDTO;
import dasturlash.uz.entity.Comment;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserRole;

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
        newComment.setCreatedDate(LocalDateTime.now());

        commentRepository.save(newComment);

        return "New comment created with id: " + newComment.getId();
    }


    public String updateComment(CommentUpdateDTO request) {

        Comment existingComment = getCommentEntityById(request.getId());

        // Validate user permission
        if (!existingComment.getProfileId().equals(getCurrentUserId()) || !isAdmin()) {
            throw new ForbiddenException("You are not authorized to update this comment");
        }

        existingComment.setContent(request.getContent());
        existingComment.setUpdatedDate(LocalDateTime.now());

        commentRepository.save(existingComment);

        return "Comment updated successfully with id: " + request.getId();
    }

    public String deleteCommentById(String commentId) {

        Comment existingComment = getCommentEntityById(commentId);

        if (existingComment == null) {
            return "Comment not found";
        }

        // Validate user permission
        if (!existingComment.getProfileId().equals(getCurrentUserId())) {
            throw new ForbiddenException("You are not authorized to delete this comment");
        }

        commentRepository.delete(existingComment);
        return "Comment deleted successfully";

    }


    private Comment getCommentEntityById(String commendId) {

        if (commendId == null) {
            return null;
        }

        return commentRepository.findById(commendId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));

    }

    private boolean isAdmin() {
        // Logic to determine if the user is an admin
        return getCurrentUserRole() == ProfileRole.ROLE_ADMIN;
    }

}
