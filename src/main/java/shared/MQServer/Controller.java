package shared.MQServer;

import com.rabbitmq.client.*;
import org.json.JSONObject;
import shared.MQSubscriptions.ExecutionPair;
import shared.MQSubscriptions.MQSubscriptions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static shared.Helpers.blockAndSubscribe;
import static shared.Helpers.getResponseQueue;
import static utilities.Main.generateUUID;

public abstract class Controller {
    private static MQSubscriptions mqSubscriptions = new MQSubscriptions(RMQConnection.getSingleton());

    public abstract JSONObject execute(JSONObject jsonObject, String userId) throws Exception;

    public synchronized static JSONObject send(String serviceName, String receiverName, JSONObject jsonObject) throws IOException, InterruptedException {
        String uuid = generateUUID();

        jsonObject.put("uuid", uuid);
        jsonObject.put("sender", serviceName);

        Connection connection = RMQConnection.getSingleton();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(receiverName, true, false, false, null);
        channel.basicPublish("", receiverName, null,
                jsonObject.toString().getBytes("UTF-8"));


        return blockAndSubscribe(mqSubscriptions, uuid, serviceName, receiverName);
    }

}
