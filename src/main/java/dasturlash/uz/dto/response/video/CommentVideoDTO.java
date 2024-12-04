package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVideoDTO {

    private String id;
    private String title;
    private MediaUrlDTO previewAttach;
    private Integer viewCount;
    private String duration;
    private LocalDateTime publishedDate;

    // Constructor to initialize from VideoShortInfoDTO, excluding channel
    public CommentVideoDTO(VideoShortInfoDTO videoShortInfoDTO) {
        this.id = videoShortInfoDTO.getId();
        this.title = videoShortInfoDTO.getTitle();
        this.previewAttach = videoShortInfoDTO.getPreviewAttach();
        this.viewCount = videoShortInfoDTO.getViewCount();
        this.duration = videoShortInfoDTO.getDuration();
        this.publishedDate = videoShortInfoDTO.getPublishedDate();
    }
}
