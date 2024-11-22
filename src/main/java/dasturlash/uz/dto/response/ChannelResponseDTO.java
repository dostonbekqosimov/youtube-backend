package dasturlash.uz.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dasturlash.uz.entity.Attach;
import dasturlash.uz.entity.Profile;
import dasturlash.uz.enums.ChannelStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelResponseDTO {

    private String id;

    private String name;

    private String description;

    private String photoId;

    private Long profileId;

    private String bannerId;

    private ChannelStatus status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Boolean visible;

    private String handle;
}
