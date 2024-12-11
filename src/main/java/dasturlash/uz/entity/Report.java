package dasturlash.uz.entity;

import dasturlash.uz.enums.ReportType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private Profile profile;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType type;


}
