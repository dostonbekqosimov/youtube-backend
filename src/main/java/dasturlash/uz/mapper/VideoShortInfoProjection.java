package dasturlash.uz.mapper;

import java.time.LocalDateTime;

public interface VideoShortInfoProjection {
    String getId();
    String getTitle();
    String getPreviewAttachId();
    String getChannelId();
//    String getChannelName();
//    String getChannelPhotoId();
    Integer getViewCount();
    String getDuration();
    LocalDateTime getPublishedDate();
}


