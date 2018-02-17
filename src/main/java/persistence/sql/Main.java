package persistence.sql;

import org.javalite.activejdbc.Base;

import java.io.IOException;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Main {
    public static void openConnection() throws IOException {
        Properties properties = readPropertiesFile("src/main/resources/config.properties");
        Base.open(
                properties.getProperty("POSTGRESQL_DRIVER"),
                properties.getProperty("POSTGRESQL_URL"),
                properties.getProperty("POSTGRESQL_USER"),
                properties.getProperty("POSTGRESQL_PASSWORD")
        );
    }

    public static void closeConnection() {
        Base.close();
    }
}
