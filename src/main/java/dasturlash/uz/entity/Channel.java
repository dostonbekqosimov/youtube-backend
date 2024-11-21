package dasturlash.uz.entity;


import dasturlash.uz.enums.ChannelStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist")
@Data
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "photo_id")
    private String photoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    private Attach photo;

    @Column(name = "profile_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private Profile profile;

    @Column(name = "banner_id")
    private String bannerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id", updatable = false, insertable = false)
    private Attach banner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChannelStatus status;

    // Bularni men o'zim qo'shdim(Doston)
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "visible")
    private Boolean visible;
}
