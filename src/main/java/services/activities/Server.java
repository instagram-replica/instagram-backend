package services.activities;

import shared.Settings;


public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/configs/activities/default_config.json";

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;
        Settings.init(fileUri);

        Controller controller = new Controller();
        shared.mq_server.Server.run(controller);
    }
}