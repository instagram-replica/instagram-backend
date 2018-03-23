package services.activities;

import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import static utilities.Main.readPropertiesFile;

public class Controller extends shared.mq_server.Controller {

    Properties props;

    public Controller(){
        super();
        try {
            props = readPropertiesFile("src/main/resources/activities_mapper.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();
        JSONObject error = new JSONObject();

        String methodName = jsonObject.getString("method");
        String methodSignature = props.getProperty(methodName);
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        try {
            Method method = NotificationActions.class.getMethod(methodSignature, JSONObject.class, String.class);
            newJsonObj = (JSONObject) method.invoke(null,paramsObject, userId);
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
            error.put("description","Internal Server Error");
        }

        JSONObject response = new JSONObject();
        response.put("error",error);
        response.put("data",newJsonObj);
        return response;
    }
}
