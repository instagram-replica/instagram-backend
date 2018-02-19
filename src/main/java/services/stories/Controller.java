package services.stories;

import org.json.JSONObject;

public class Controller extends shared.Controller {


    public Controller() {
        super();
    }

    @Override
    public JSONObject execute(JSONObject jsonObject, String userId) {
        JSONObject newJsonObj = new JSONObject();

        String uri = jsonObject.get("uri").toString().substring(2);

        String[] parameters = uri.split("&");

        for(int i =0; i< parameters.length;i++){
            String[] parameter = parameters[i].split("=");
            String key = parameter[0];
            String value = parameter[1];
            if(key.equals("method")){
                switch (value) {
                    case "createStory":  createStory(parameters);
                        break;
                    case "deleteStory":  deleteStory(parameters);
                        break;
                    case "getMyStory":  getMyStory(parameters);
                        break;
                    case "getDiscoverStories":  getDiscoverStories(parameters);
                        break;
                    default: newJsonObj.put("error", "unspecified method name");
                        return newJsonObj;
                }
            }
        }

        newJsonObj.put("application", "feed/stories");
        return newJsonObj;
    }
}
