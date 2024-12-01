create function increment_share_count() returns trigger
    language plpgsql
as
$$
BEGIN
UPDATE videos
SET shared_count = COALESCE(shared_count, 0) + 1
WHERE id = NEW.video_id;

RETURN NEW;
END;
$$;

alter function increment_share_count() owner to postgres;

