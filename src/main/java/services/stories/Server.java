package services.stories;


import shared.Settings;


public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/java/services/stories/config/default_config.json";

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;

        Controller controller = new Controller();

        shared.mq_server.Server server = new shared.mq_server.Server(Settings.readSettingsFromFile(fileUri));

        server.run(controller);

    }
}
