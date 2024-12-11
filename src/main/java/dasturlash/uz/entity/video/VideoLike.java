package dasturlash.uz.entity.video;

import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.LikeType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "video_like")
public class VideoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private Profile profile;

    @Column(name = "video_id", nullable = false)
    private String videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private Video video;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LikeType type;
}
