package http_server;

import shared.mq_subscriptions.MQSubscriptions;
import shared.Settings;

public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/java/http_server/config/default_config.json";
    public static MQSubscriptions mqSubscriptions = new MQSubscriptions(RMQConnection.getSingleton());

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;

        Settings settings = Settings.readSettingsFromFile(fileUri);

        shared.http_server.Server.start(settings, new ServerInitializer(settings));
    }
}
