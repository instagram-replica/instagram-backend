package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Thread {
    UUID id;
    UUID creator_id;
    ArrayList<String> user_ids;
    String name;
    Timestamp created_at;
    Timestamp deleted_at;
    Timestamp blocked_at;
    ArrayList<Message> messages;

    public Thread(UUID creator_id, ArrayList<String> user_ids, String name) {
        this.creator_id = creator_id;
        this.user_ids = user_ids;
        this.name = name;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.messages = new ArrayList<Message>();
    }
}
