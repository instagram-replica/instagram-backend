package shared.MQSubscriptions;

import org.json.JSONObject;

public interface Exec {
    void onMessageReceived(JSONObject jsonObject);
}
