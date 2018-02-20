package persistence.nosql.Datatypes;

import java.util.UUID;

public class UserActivityType extends ActivityType {
    UUID user_id;

    public UserActivityType(UUID user_id) {
        this.id = UUID.randomUUID();
        this.user_id = user_id;
        this.type=Type.FOLLOWING;
    }
}
