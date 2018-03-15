package shared.http_server.routes;

import org.json.JSONObject;
import shared.Settings;
import shared.http_server.Server;

import java.io.IOException;

public class ServerController {
    public static JSONObject shutdown(Object param) {
        try {
            Server.close();
            System.out.println("Server was shutdown by external signal!");
            return new JSONObject().put("success", true);
        } catch (IOException e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }

    public static JSONObject getInfo(Object param) {
        return Settings.getInstance().toJSON();
    }
}
