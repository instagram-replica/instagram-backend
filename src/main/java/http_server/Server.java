package http_server;

import shared.RMQConnection;
import shared.mq_subscriptions.MQSubscriptions;
import shared.Settings;

public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/configs/http_server/default_config.json";
    public static MQSubscriptions mqSubscriptions = new MQSubscriptions(RMQConnection.getSingleton());

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;

        Settings.init(fileUri);

        shared.http_server.Server.start(new ServerInitializer());
    }
}
