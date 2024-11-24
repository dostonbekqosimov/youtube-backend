package dasturlash.uz.entity.video;

import dasturlash.uz.entity.Attach;
import dasturlash.uz.entity.Category;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.enums.ContentStatus;
import dasturlash.uz.enums.VideoType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Data
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "preview_attach_id")
    private String previewAttachId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_attach_id", updatable = false, insertable = false)
    private Attach preview;

    @Column(name = "title")
    private String title;

    @Column(name = "playlist_id")
    private String playlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", updatable = false, insertable = false)
    private Playlist playlist;


    @Column(name = "category_id")
    private Long categoryId;

    @OneToOne
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    private Category category;

    @Column(name = "attach_id")
    private String attachId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", updatable = false, insertable = false)
    private Attach video;

    @Column(name = "channel_id")
    private String channelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", updatable = false, insertable = false)
    private Channel channel;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VideoType type;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "shared_count")
    private Integer sharedCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "dislike_count")
    private Integer dislikeCount;

    // Buni shunchaki men qo'shdim (Doston)
    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;


}
