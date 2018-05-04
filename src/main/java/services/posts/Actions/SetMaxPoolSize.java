package services.posts.Actions;

import org.json.JSONObject;
import shared.mq_server.Server;

public class SetMaxPoolSize  implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId, String methodName) {
        try {
            int maxThreadCount = Integer.parseInt(jsonObject.getString("maxThreadCount"));
            System.out.println(Server.executor);
            Server.executor.setMaximumPoolSize(maxThreadCount);
            Server.channel.basicQos(maxThreadCount);
            return new JSONObject().put("status", maxThreadCount + "");
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("status", " Internal Server Error");
        }
    }
}