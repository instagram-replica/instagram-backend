package persistence.sql;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Database {
  private static BasicDataSource dataSource;

  private Database() {}

  public static void openConnection() throws IOException {
    Properties properties = readPropertiesFile("src/main/resources/postgres.properties");

    String POSTGRESQL_USER = properties.getProperty("POSTGRESQL_USER");
    String POSTGRESQL_PASSWORD = properties.getProperty("POSTGRESQL_PASSWORD");
    String POSTGRESQL_DRIVER = properties.getProperty("POSTGRESQL_DRIVER");
    String POSTGRESQL_URL = properties.getProperty("POSTGRESQL_URL");

    dataSource = new BasicDataSource();

    dataSource.setUsername(POSTGRESQL_USER);
    dataSource.setPassword(POSTGRESQL_PASSWORD);
    dataSource.setDriverClassName(POSTGRESQL_DRIVER);
    dataSource.setUrl(POSTGRESQL_URL);
    dataSource.setInitialSize(10);
  }

  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
