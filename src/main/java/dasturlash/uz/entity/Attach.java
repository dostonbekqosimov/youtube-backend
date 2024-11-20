package dasturlash.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Table(name = "attaches")
@Getter
public class Attach {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "origin_name")
    private String originName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String path;

    @Column
    private Long duration;

    @Column(nullable = false)
    private LocalDateTime createdDate;

}
