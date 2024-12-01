package dasturlash.uz.service;

import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.entity.Comment;
import dasturlash.uz.repository.CommentRepository;
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


}
