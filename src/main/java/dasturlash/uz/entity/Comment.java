package dasturlash.uz.entity;

import dasturlash.uz.entity.video.Video;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String content;

    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private Profile profile;

    @Column(name = "video_id")
    private String videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", updatable = false, insertable = false)
    private Video video;

    @Column(name = "reply_id")
    private String replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", updatable = false, insertable = false)
    private Comment comment;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "dislike_count")
    private Integer dislikeCount;

    private Boolean visible;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}
