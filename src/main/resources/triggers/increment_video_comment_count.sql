create function increment_video_comment_count() returns trigger
    language plpgsql
as
$$
BEGIN
    -- Update video's comment count, handling null cases
UPDATE videos
SET comment_count = COALESCE(comment_count, 0) + 1
WHERE id = NEW.video_id;

RETURN NEW;
END;
$$;

alter function increment_video_comment_count() owner to postgres;

