package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Comment {
    UUID id;
    String text;
    boolean isDeep;
    UUID user_id;
    UUID post_id;
    ArrayList<Comment> comments;
    Timestamp created_at;
    Timestamp updated_at;
    Timestamp deleted_at;
    Timestamp blocked_at;

    public Comment(String text, boolean isDeep, UUID user_id, UUID post_id) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.isDeep = isDeep;
        this.user_id = user_id;
        this.post_id = post_id;
        this.comments = new ArrayList<Comment>();
        this.created_at = new Timestamp(System.currentTimeMillis());
    }
}
