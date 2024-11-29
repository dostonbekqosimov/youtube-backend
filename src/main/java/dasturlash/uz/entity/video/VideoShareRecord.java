package dasturlash.uz.entity.video;

import dasturlash.uz.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "video_share_record")
public class VideoShareRecord {




    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  id;

    @Column(name = "video_id")
    private String videoId;

    @ManyToOne
    @JoinColumn(name = "video_id", updatable = false, insertable = false)
    private Video video;

    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private Profile profile;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    }



