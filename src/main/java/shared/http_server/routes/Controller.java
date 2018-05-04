package shared.http_server.routes;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Controller {

    public static JSONObject execute(String methodName, JSONObject params) {

        try {

            ClassLoader classLoader = ServerController.class.getClassLoader();
            Class controllerClass = classLoader.loadClass("shared.http_server.routes.ServerController");

            Properties props = readPropertiesFile("src/main/resources/http_routes.properties");

            Method method = controllerClass.getMethod(props.getProperty(methodName), JSONObject.class);
            Object res = method.invoke(null, params);
            return (JSONObject) res;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException | ClassNotFoundException e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }
}
