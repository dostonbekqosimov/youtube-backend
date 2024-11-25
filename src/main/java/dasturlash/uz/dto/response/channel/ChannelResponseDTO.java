package dasturlash.uz.dto.response.channel;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.enums.ChannelStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelResponseDTO {

    private String id;

    private String name;

    private String description;

    private MediaUrlDTO photo;

    private Long profileId;

    private MediaUrlDTO banner;

    private ChannelStatus status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Boolean visible;

    private String handle;
}
