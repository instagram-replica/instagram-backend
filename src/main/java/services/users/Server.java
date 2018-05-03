package services.users;

import persistence.sql.Migrations;
import shared.Settings;

import java.io.IOException;

public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/configs/users/default_config.json";

    public static void main(String[] args) throws IOException {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;
        Settings.init(fileUri);

        Controller controller = new Controller();
        shared.mq_server.Server.run(controller);
    }
}
