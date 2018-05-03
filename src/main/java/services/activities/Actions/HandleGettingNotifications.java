package services.activities.Actions;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.cache.ActivitiesCache;
import persistence.nosql.ActivityMethods;

public class HandleGettingNotifications implements Action {

    public static JSONObject execute(JSONObject jsonObject, String userId) {
        int size = jsonObject.getInt("pageSize");
        int start = jsonObject.getInt("pageIndex") * size;

        JSONArray notifications = ActivitiesCache.getNotificationsFromCache(userId, start, size);
        if(notifications==null) {
            notifications= ActivityMethods.getNotifications(userId, start, size);
            ActivitiesCache.insertNotificationsIntoCache(notifications,userId, start, size);
        }

        JSONObject result = new JSONObject();
        result.put("notifications", notifications);
        return result;
    }
}
