package shared;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import static utilities.Main.readPropertiesFile;

public class RMQConnection {
    private static Connection connection;
    private static int connectionTrials = 5;

    private RMQConnection() {

    }

    public synchronized static Connection getSingleton() {
        if (RMQConnection.connection == null) {
            try {
                RMQConnection.connection = tryToConnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RMQConnection.connection;
    }

    private synchronized static Connection tryToConnect() throws Exception {
        try {
            Properties properties = readPropertiesFile("src/main/resources/rmq.properties");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getProperty("host"));
            factory.setPort(Integer.parseInt(properties.getProperty("port")));
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            if (connectionTrials-- > 0) {
                System.out.println("Trying to reconnect to RMQ!");
                System.out.println("Sleeping for 5 seconds");
                Thread.sleep(5000);
                return tryToConnect();

            }
            //TODO: Implement custom exception
            throw new Exception("Failed to connect to RMQ!");
        }
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
