package persistence.nosql.Datatypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Hashtag {
    String text;
    ArrayList<UUID> follower_ids;
    ArrayList<UUID> post_ids;
    Timestamp created_at;
    Timestamp updated_at;
    Timestamp blocked_at;

    public Hashtag(String text) {
        this.text = text;
        this.follower_ids = new ArrayList<UUID>();
        this.post_ids = new ArrayList<UUID>();
        this.created_at = new java.sql.Timestamp(System.currentTimeMillis());
    }
}
