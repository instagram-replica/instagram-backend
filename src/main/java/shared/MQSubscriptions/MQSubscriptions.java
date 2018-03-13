package shared.MQSubscriptions;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MQSubscriptions {
    private Connection connection;
    private HashMap<String, ArrayList<ExecutionPair>> map = new HashMap<>();

    public MQSubscriptions(Connection connection) {
        this.connection = connection;
    }

    private void createConsumer(String queueName) throws IOException {
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, true, false, false, null);
        channel.basicConsume(queueName, true, (consumerTag, msg) -> {

            String message = new String(msg.getBody(), "UTF-8");
            JSONObject jsonObject = new JSONObject(message);
            //FIXEME:

            ArrayList<ExecutionPair> executionPairs = (ArrayList<ExecutionPair>) map.get(queueName).clone();
            if (executionPairs == null) return;

            for (ExecutionPair executionPair : executionPairs) {
                if (jsonObject.getString("uuid").equals(executionPair.UUID)) {
                    jsonObject.remove("uuid");
                    executionPair.exec.onMessageReceived(jsonObject);
                    map.get(queueName).remove(executionPair);
                }
            }
        }, (consumerTag, sig) -> System.out.println("Shut Down Signal!!!!!!!!!"));
    }

    public void addListener(String queueName, ExecutionPair executionPair) throws IOException {
        if (map.containsKey(queueName)) {
            map.get(queueName).add(executionPair);
        } else {
            ArrayList<ExecutionPair> execArray = new ArrayList<>();
            execArray.add(executionPair);
            map.put(queueName, execArray);
            createConsumer(queueName);
        }

    }
}
