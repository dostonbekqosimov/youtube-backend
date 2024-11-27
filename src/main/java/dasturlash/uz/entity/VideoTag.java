package dasturlash.uz.entity;

import dasturlash.uz.entity.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "video_tags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"video_id", "tag_id"})})
public class VideoTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private Boolean visible;

    public VideoTag(Video video, Tag tag, Boolean visible) {
        this.video = video;
        this.tag = tag;
        this.visible = visible;
    }
}

