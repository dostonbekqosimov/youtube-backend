package dasturlash.uz.mapper;

import java.time.LocalDateTime;

public interface AdminVideoProjection {
    String getId(); // Video ID
    String getTitle(); // Video title
    String getPreviewAttachId(); // Preview attachment ID
    String getChannelId(); // Channel ID
    String getOwnerId(); // Owner ID (via Channel)
    String getOwnerName(); // Owner name (via Channel)
    String getOwnerSurname(); // Owner surname (via Channel)
    String getPlaylistId(); // Playlist ID
    String getPlaylistName(); // Playlist name
    Integer getViewCount(); // View count
    String getDuration(); // Duration
    LocalDateTime getPublishedDate(); // Published date
}


