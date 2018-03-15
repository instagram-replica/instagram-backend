package services.posts;


import shared.Settings;


public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/java/services/posts/config/default_config.json";

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;

        Settings settings = Settings.readSettingsFromFile(fileUri);

        Controller controller = new Controller(settings);

        shared.mq_server.Server server = new shared.mq_server.Server(settings);

        server.run(controller);

    }
}