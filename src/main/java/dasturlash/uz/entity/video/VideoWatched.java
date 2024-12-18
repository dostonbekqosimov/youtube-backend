package dasturlash.uz.entity.video;

import dasturlash.uz.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_watched")
@Setter
@Getter
public class VideoWatched {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "profile_id")
    private Long profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private Profile profile;

    @Column(name = "video_id", nullable = false)
    private String videoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private Video video;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_browser")
    private String userBrowser;

    @Column(nullable = false)
    private LocalDateTime createdDate;
}
