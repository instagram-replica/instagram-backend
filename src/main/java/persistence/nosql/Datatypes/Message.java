package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Message {
    UUID id;
    String text;
    UUID user_id;
    Timestamp created_at;
    Timestamp deleted_at;
    Timestamp blocked_at;
    ArrayList<UUID> liker_ids;
    UUID media_id;

    public Message(String text, UUID user_id, UUID media_id) {

        this.id = UUID.randomUUID();
        this.text = text;
        this.user_id = user_id;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.liker_ids = new ArrayList<UUID>();
        this.media_id = media_id;
    }

    public Message(UUID id, String text, UUID user_id, Timestamp created_at, Timestamp deleted_at, Timestamp blocked_at, ArrayList<UUID> liker_ids, UUID media_id) {
        this.id = id;
        this.text = text;
        this.user_id = user_id;
        this.created_at = created_at;
        this.deleted_at = deleted_at;
        this.blocked_at = blocked_at;
        this.liker_ids = liker_ids;
        this.media_id = media_id;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public Timestamp getDeletedAt() {
        return deleted_at;
    }

    public Timestamp getBlockedAt() {
        return blocked_at;
    }

    public ArrayList<UUID> getLikerIds() {
        return liker_ids;
    }

    public UUID getMediaId() {
        return media_id;
    }

    public UUID getUserId() {
        return user_id;
    }
}
