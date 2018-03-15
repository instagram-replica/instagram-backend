package shared.mq_server;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import static utilities.Main.readPropertiesFile;

public class RMQConnection {
    static Connection connection;

    private RMQConnection() {

    }

    public synchronized static Connection getSingleton() {
        if (RMQConnection.connection == null) {
            try {
                Properties properties = readPropertiesFile("src/main/resources/rmq.properties");
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(properties.getProperty("host"));
                factory.setPort(Integer.parseInt(properties.getProperty("port")));
                RMQConnection.connection = factory.newConnection();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }

        }
        return RMQConnection.connection;
    }

    public static void close() {
        if (RMQConnection.connection != null) {
            try {
                RMQConnection.connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
