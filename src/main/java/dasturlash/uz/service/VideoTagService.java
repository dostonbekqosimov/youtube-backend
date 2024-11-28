package dasturlash.uz.service;

import dasturlash.uz.entity.Tag;
import dasturlash.uz.entity.VideoTag;
import dasturlash.uz.entity.video.Video;
import dasturlash.uz.repository.VideoTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoTagService {

    private final VideoTagRepository videoTagRepository;

    public void updateVideoTags(Video video, List<Tag> tags) {

        if (tags == null || tags.isEmpty()) {
            videoTagRepository.deleteByVideo(video);
            return;
        }
        // Retrieve all current VideoTag entries for the video
        List<VideoTag> currentTags = videoTagRepository.findAllVideoTagsByVideo(video);

        // Map current VideoTags for quick lookup
        Map<String, VideoTag> currentTagMap = currentTags.stream()
                .collect(Collectors.toMap(vt -> vt.getTag().getName(), vt -> vt));

        // Process the new tags
        for (Tag tag : tags) {
            VideoTag existingVideoTag = currentTagMap.get(tag.getName());

            if (existingVideoTag != null) {
                // Reactivate existing tag if it was invisible
                if (!existingVideoTag.getVisible()) {
                    existingVideoTag.setVisible(true);
                    videoTagRepository.save(existingVideoTag);
                }
            } else {
                // Create and save new VideoTag entry
                VideoTag newVideoTag = new VideoTag(video, tag, true);
                videoTagRepository.save(newVideoTag);
            }
        }

        // Mark tags not in the new list as invisible
        for (VideoTag currentTag : currentTags) {
            if (!tags.contains(currentTag.getTag())) {
                currentTag.setVisible(false);
                videoTagRepository.save(currentTag);
            }
        }
    }


    @Transactional
    public void reactivateSoftDeletedTags(Video video, List<Tag> tags) {
        tags.forEach(tag -> {
            Optional<VideoTag> existingVideoTag = videoTagRepository.findSoftDeletedVideoTag(video.getId(), tag.getId());
            if (existingVideoTag.isPresent()) {
                VideoTag videoTag = existingVideoTag.get();
                videoTag.setVisible(true);
                videoTagRepository.save(videoTag);
            }
        });
    }

    @Transactional
    public void createNewVideoTags(Video video, List<Tag> tagsToAdd) {
        List<VideoTag> newVideoTags = tagsToAdd.stream()
                .map(tag -> {
                    VideoTag videoTag = new VideoTag();
                    videoTag.setVideo(video);
                    videoTag.setTag(tag);
                    videoTag.setVisible(Boolean.TRUE);
                    return videoTag;
                })
                .collect(Collectors.toList());

        videoTagRepository.saveAll(newVideoTags);
    }

    public List<String> getVisibleTagNamesForVideo(Video video) {
        return videoTagRepository.findVisibleTagNamesByVideo(video);
    }
}