package dasturlash.uz.dto.response;

import lombok.Data;

@Data
public class PlaylistInfoDTO {
    private String id;
    private String name;

    public PlaylistInfoDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
