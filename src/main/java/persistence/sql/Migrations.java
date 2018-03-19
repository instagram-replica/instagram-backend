package persistence.sql;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Migrations {
    public static void main(String[] args) throws IOException {
        Properties properties = readPropertiesFile("src/main/resources/postgres.properties");

        Flyway flyway = new Flyway();

        flyway.setDataSource(
                properties.getProperty("POSTGRESQL_URL"),
                properties.getProperty("POSTGRESQL_USER"),
                properties.getProperty("POSTGRESQL_PASSWORD")
        );

        flyway.migrate();
    }

}
