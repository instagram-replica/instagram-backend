package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.UUID;

public class Notification {
    UUID id;
    ActivityType type;
    UUID receiver_id;
    UUID sender_id;
    Timestamp created_at;
    Timestamp blocked_at;

    public Notification(ActivityType type, UUID receiver_id, UUID sender_id) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.created_at = new Timestamp(System.currentTimeMillis());
    }
}
