package dasturlash.uz.entity.video;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "video_view_record")
public class VideoViewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  id;

    @Column(name = "video_id")
    private String videoId;

    @ManyToOne
    @JoinColumn(name = "video_id", updatable = false, insertable = false)
    private Video video;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}