package dasturlash.uz.dto.response;

import lombok.Data;

@Data
public class MediaUrlDTO {
    private String id;
    private String url;

    public MediaUrlDTO() {
    }

    public MediaUrlDTO(MediaUrlDTO urlOfMedia) {

        this.id = urlOfMedia.getId();
        this.url = urlOfMedia.getUrl();

    }

    public MediaUrlDTO(String id, String url) {
        this.id = id;
        this.url = url;
    }


}
