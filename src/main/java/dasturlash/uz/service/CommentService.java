package dasturlash.uz.service;

import dasturlash.uz.dto.response.comment.AdminCommentInfoDTO;
import dasturlash.uz.dto.request.CommentCreateDTO;
import dasturlash.uz.dto.request.comment.CommentUpdateDTO;
import dasturlash.uz.dto.response.comment.CommentInfoDTO;
import dasturlash.uz.dto.response.video.VideoShortInfoDTO;
import dasturlash.uz.entity.Comment;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.ForbiddenException;
import dasturlash.uz.repository.CommentRepository;
import dasturlash.uz.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserRole;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final VideoService videoService;
    private final ProfileService profileService;

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
        if (!existingComment.getProfileId().equals(getCurrentUserId()) || isAdmin()) {
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
        return getCurrentUserRole() != ProfileRole.ROLE_ADMIN;
    }

    public PageImpl<AdminCommentInfoDTO> getAllComments(int page, int size) {
        // Check if the user is an admin
        if (isAdmin()) {
            throw new ForbiddenException("You are not authorized to access this resource");
        }

        // Fetch comments with pagination
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate"));
        Page<Comment> commentList = commentRepository.findAll(pageRequest);

        // Extract unique video IDs from comments
        List<String> videoIdList = new ArrayList<>();
        for (Comment comment : commentList.getContent()) {
            String videoId = comment.getVideoId();
            if (!videoIdList.contains(videoId)) {
                videoIdList.add(videoId); // Ensure uniqueness
            }
        }

        // Fetch video details for all video IDs in a batch
        List<VideoShortInfoDTO> videoList = videoService.getVideoShortInfoByVideoIds(videoIdList);

        // Map comments to DTOs, setting video details
        List<AdminCommentInfoDTO> adminCommentInfoList = new ArrayList<>();
        for (Comment comment : commentList.getContent()) {
            AdminCommentInfoDTO adminComment = toAdminCommentInfoDTO(comment);

            // Find the corresponding video details
            VideoShortInfoDTO videoDetails = null;
            for (VideoShortInfoDTO video : videoList) {
                if (video.getId().equals(comment.getVideoId())) {
                    videoDetails = video;
                    video.setChannel(null);
                    break;
                }
            }

            adminComment.setVideoDetails(videoDetails);
            adminCommentInfoList.add(adminComment);
        }

        return new PageImpl<>(adminCommentInfoList, pageRequest, commentList.getTotalElements());
    }

    public List<AdminCommentInfoDTO> getCommentListByProfileId(Long profileId) {

        // Check if the user is an admin
        if (isAdmin()) {
            throw new ForbiddenException("You are not authorized to access this resource");
        }

        List<Comment> commentList = commentRepository.findAllByProfileId(profileId);

        // Extract unique video IDs from comments
        List<String> videoIdList = new ArrayList<>();
        for (Comment comment : commentList) {
            String videoId = comment.getVideoId();
            if (!videoIdList.contains(videoId)) {
                videoIdList.add(videoId); // Ensure uniqueness
            }
        }

        // Fetch video details for all video IDs in a batch
        List<VideoShortInfoDTO> videoList = videoService.getVideoShortInfoByVideoIds(videoIdList);


        // Map comments to DTOs, setting video details
        List<AdminCommentInfoDTO> commentInfoList = new ArrayList<>();
        for (Comment comment : commentList) {
            AdminCommentInfoDTO commentInfoDTO = toAdminCommentInfoDTO(comment);

            // Find the corresponding video details
            VideoShortInfoDTO videoDetails = null;
            for (VideoShortInfoDTO video : videoList) {
                if (video.getId().equals(comment.getVideoId())) {
                    videoDetails = video;
                    video.setChannel(null);
                    break;
                }
            }

            commentInfoDTO.setVideoDetails(videoDetails);


            commentInfoList.add(commentInfoDTO);


        }
        return commentInfoList;


    }

    public PageImpl<CommentInfoDTO> getCommentListByVideoId(int page, int size, String videoId) {

        // Fetch comments with pagination
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate"));
        Page<Comment> commentList = commentRepository.findAllByVideoId(pageRequest, videoId);

        // Extract unique video IDs from comments
        List<String> videoIdList = new ArrayList<>();
        for (Comment comment : commentList.getContent()) {
            String videoIdInComment = comment.getVideoId();
            if (!videoIdList.contains(videoIdInComment)) {
                videoIdList.add(videoIdInComment); // Ensure uniqueness
            }
        }

        // Fetch video details for all video IDs in a batch
        List<VideoShortInfoDTO> videoList = videoService.getVideoShortInfoByVideoIds(videoIdList);


        // Map comments to DTOs, setting video details
        List<CommentInfoDTO> commentInfoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentInfoDTO commentInfoDTO = toCommentInfoDTO(comment);

            // Find the corresponding video details
            VideoShortInfoDTO videoDetails = null;
            for (VideoShortInfoDTO video : videoList) {
                if (video.getId().equals(comment.getVideoId())) {
                    videoDetails = video;
                    video.setChannel(null);
                    break;
                }
            }

            commentInfoDTO.setVideoDetails(videoDetails);

            commentInfoDTO.setProfile(profileService.getShortInfo(comment.getProfileId()));

            commentInfoList.add(commentInfoDTO);


        }
        return new PageImpl<>(commentInfoList, pageRequest, commentList.getTotalElements());
    }


    private CommentInfoDTO toCommentInfoDTO(Comment comment) {
        CommentInfoDTO dto = new CommentInfoDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setUpdatedDate(comment.getUpdatedDate());
        dto.setLikeCount(comment.getLikeCount());
        dto.setDislikeCount(comment.getDislikeCount());

        // Exclude video details; handled in the main method
        return dto;
    }


    private AdminCommentInfoDTO toAdminCommentInfoDTO(Comment comment) {
        AdminCommentInfoDTO dto = new AdminCommentInfoDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setUpdatedDate(comment.getUpdatedDate());
        dto.setLikeCount(comment.getLikeCount());
        dto.setDislikeCount(comment.getDislikeCount());

        // Exclude video details; handled in the main method
        return dto;
    }


}
