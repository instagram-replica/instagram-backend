package persistence.nosql.Datatypes;

import java.util.UUID;

public class PostActivityType extends ActivityType{
    UUID post_id;

    public PostActivityType(UUID post_id, Type type) {
        this.id = UUID.randomUUID();
        this.post_id = post_id;
        this.type = type;
    }
}
