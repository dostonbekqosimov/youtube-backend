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
    private String duration;

    @Column(nullable = false)
    private LocalDateTime createdDate;


    // just add out of curiosity
    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private Boolean visible;


}
