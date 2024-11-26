package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.OwnerInfoDTO;
import dasturlash.uz.dto.response.PlaylistInfoDTO;
import lombok.Data;

@Data
public class AdminVideoInfoDTO {
    private VideoShortInfoDTO videoShortInfo; // Video details
    private OwnerInfoDTO owner;              // Owner details
    private PlaylistInfoDTO playlist;        // Playlist details

    public AdminVideoInfoDTO(VideoShortInfoDTO videoShortInfo, OwnerInfoDTO owner, PlaylistInfoDTO playlist) {
        this.videoShortInfo = videoShortInfo;
        this.owner = owner;
        this.playlist = playlist;
    }
}
