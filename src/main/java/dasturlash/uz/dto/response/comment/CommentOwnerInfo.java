package dasturlash.uz.dto.response.comment;

import dasturlash.uz.dto.response.MediaUrlDTO;
import lombok.Data;

@Data
public class CommentOwnerInfo {
    private String id;
    private String name;
    private String surname;
    private MediaUrlDTO photo;
}
