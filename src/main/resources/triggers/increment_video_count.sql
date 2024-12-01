create function increment_view_count() returns trigger
    language plpgsql
as
$$
BEGIN
    -- Increment the view_count for the associated video in the videos table
UPDATE videos
SET view_count = COALESCE(view_count, 0) + 1
WHERE id = NEW.video_id;

RETURN NEW;
END;
$$;

alter function increment_view_count() owner to postgres;

