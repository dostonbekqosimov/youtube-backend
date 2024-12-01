package dasturlash.uz.mapper;

import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.video.VideoMediaDTO;

public interface VideoWatchedHistory {
    String getTitle();
    String getChannelName();
    String getViewCount();
    String getAttachId();

}
