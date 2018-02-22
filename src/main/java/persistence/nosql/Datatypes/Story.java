package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Story {
    UUID id;
    UUID user_id;
    boolean is_featured;
    UUID media_id;
    ArrayList<Report> report;
    ArrayList<UUID> seen_by_user_ids;
    Timestamp created_at;
    Timestamp expired_at;
    Timestamp deleted_at;
    Timestamp blocked_at;

    public Story(UUID user_id, boolean is_featured, UUID media_id) {
        this.id = UUID.randomUUID();
        this.user_id = user_id;
        this.is_featured = is_featured;
        this.media_id = media_id;
        this.report = new ArrayList<Report>();
        this.seen_by_user_ids = new ArrayList<UUID>();
        this.created_at = new Timestamp(System.currentTimeMillis());
    }
}
