package dasturlash.uz.entity.video;

import dasturlash.uz.entity.Attach;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.enums.ContentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist")
@Data
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus status;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "channel_id")
    private String channelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", updatable = false, insertable = false)
    private Channel channel;

    // Bularni men o'zim qo'shdim(Doston)
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "preview_id")
    private String previewId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_id", updatable = false, insertable = false)
    private Attach attach;
}
