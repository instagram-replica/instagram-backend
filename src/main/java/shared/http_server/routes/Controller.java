package shared.http_server.routes;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Controller {

    public static JSONObject execute(String methodName) {
        Object[] parameters = {new Object()};

        try {
            Properties props = readPropertiesFile("src/main/resources/http_routes.properties");
            Method method = ServerController.class.getMethod(props.getProperty(methodName), Object.class);

            Object res = method.invoke(null, new Object[]{parameters});
            return (JSONObject) res;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }
}
