create function decrement_video_comment_count() returns trigger
    language plpgsql
as
$$
BEGIN
    -- Update video's comment count, ensuring it doesn't go below 0
UPDATE videos
SET comment_count = GREATEST(COALESCE(comment_count, 1) - 1, 0)
WHERE id = OLD.video_id;

RETURN OLD;
END;
$$;

alter function decrement_video_comment_count() owner to postgres;

