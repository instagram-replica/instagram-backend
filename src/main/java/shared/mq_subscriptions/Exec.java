package shared.mq_subscriptions;

import org.json.JSONObject;

public interface Exec {
    void onMessageReceived(JSONObject jsonObject);
}
