package dasturlash.uz.dto.response.video;

import dasturlash.uz.dto.response.OwnerInfoDTO;
import dasturlash.uz.dto.response.PlaylistInfoDTO;
import lombok.Data;

@Data
public class AdminVideoInfoDTO {
    private VideoShortInfoDTO videoShortInfo;
    private OwnerInfoDTO owner;
    private PlaylistInfoDTO playlist;

    public AdminVideoInfoDTO(VideoShortInfoDTO videoShortInfo, OwnerInfoDTO owner, PlaylistInfoDTO playlist) {
        this.videoShortInfo = videoShortInfo;
        this.owner = owner;
        this.playlist = playlist;
    }
}
