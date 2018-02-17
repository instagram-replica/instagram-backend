package persistence.sql;

import org.javalite.activejdbc.Base;

import static persistence.sql.Configuration.*;

public class Main {
    public static void openConnection() {
        Base.open(
                DATABASE_DRIVER,
                DATABASE_URL,
                DATABASE_USER,
                DATABASE_PASSWORD
        );
    }

    public static void closeConnection() {
        Base.close();
    }
}
