package services.posts;

import exceptions.CustomException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import static utilities.Main.readPropertiesFile;

public class Controller extends shared.mq_server.Controller{

    private Properties props;

    public Controller(){
        super();
        try {
            props = readPropertiesFile("src/main/resources/posts_mapper.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject data = new JSONObject();
        JSONObject error = new JSONObject();

        String methodName = jsonObject.getString("method");
        String methodSignature = props.getProperty(methodName);
        JSONObject paramsObject = jsonObject.getJSONObject("params");

        try {
            Method method = PostsActions.class.getMethod(methodSignature, JSONObject.class, String.class, String.class);
            data = (JSONObject) method.invoke(null,paramsObject, userId, methodName);
        }
        catch(org.json.JSONException e){
            e.printStackTrace();
            error.put("description",utilities.Main.stringifyJSONException(e));
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
            if(e.getClass() == CustomException.class)
                error.put("description", e.getMessage());
            else
                error.put("description","Internal Server Error");
        } finally {
            JSONObject response = new JSONObject();
            response.put("error", error);
            response.put("data", data);
            return response;
        }
    }
}
