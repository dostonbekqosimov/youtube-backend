package dasturlash.uz.mapper;

public interface VideoShareProjection {
    String getId();
    String getTitle();
    String getChannelName();
    Integer getSharedCount();

    String getPreviewAttachId();
    String getAttachId();
}