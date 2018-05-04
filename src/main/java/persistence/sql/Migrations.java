package persistence.sql;

import org.flywaydb.core.Flyway;
import persistence.nosql.ArangoInterfaceMethods;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Migrations {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        //TODO: Find a better solution
        Thread.sleep(5000);
        Properties properties = readPropertiesFile("src/main/resources/postgres.properties");

        Flyway flyway = new Flyway();

        flyway.setDataSource(
                properties.getProperty("POSTGRESQL_URL"),
                properties.getProperty("POSTGRESQL_USER"),
                properties.getProperty("POSTGRESQL_PASSWORD")
        );

        flyway.migrate();


        ArangoInterfaceMethods.initializeDB();
        ArangoInterfaceMethods.initializeGraphCollections();


  }
}
