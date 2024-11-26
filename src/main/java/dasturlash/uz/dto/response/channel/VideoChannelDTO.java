package dasturlash.uz.dto.response.channel;

import dasturlash.uz.dto.response.MediaUrlDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class VideoChannelDTO {
    private String id;
    private String name;
    private String photoUrl;

    // hali qo'shilmaganlar:
    private Integer subscriberCount;
    private Boolean isSubscribed;

    public VideoChannelDTO() {
    }

    public VideoChannelDTO(VideoChannelDTO dto) {
        this.id = dto.getId();
        this.photoUrl = dto.getPhotoUrl();
        this.name = dto.getName();
    }

    public VideoChannelDTO(String id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }
}
