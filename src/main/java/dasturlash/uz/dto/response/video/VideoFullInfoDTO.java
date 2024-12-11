package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.CategoryResponseDTO;
import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.TagResponseDTO;
import dasturlash.uz.dto.response.VideoPlaylistDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.dto.response.video.like.VideoLikeDTO;
import dasturlash.uz.enums.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoFullInfoDTO {
    private String id;
    private String title;
    private String description;
    private MediaUrlDTO previewAttach;
    private VideoMediaDTO videoAttach;
    private CategoryResponseDTO category;
    private List<TagResponseDTO> tags;
    private VideoChannelDTO channel;
    private VideoLikeDTO likeDetails;
    private Integer viewCount;
    private Integer sharedCount;
    private ContentStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime publishedDate;
    private LocalDateTime scheduledDate;
    private LocalDateTime updatedDate;


    // hali ishlatilinmaganlar
    private Integer commentCount;
}
