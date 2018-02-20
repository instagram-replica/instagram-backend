package persistence.nosql.Datatypes;

import java.util.UUID;

public class Report {
    UUID user_id;
    String reason;

    public Report(UUID user_id, String reason) {
        this.user_id = user_id;
        this.reason = reason;
    }
}
