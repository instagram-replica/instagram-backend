package shared;

import com.rabbitmq.client.*;
import org.json.JSONObject;
import shared.MQServer.Queue;
import shared.MQServer.RMQConnection;

import java.io.IOException;

import static utilities.Main.generateUUID;

public abstract class Controller {
    public Controller() {

    }

    public abstract JSONObject execute(JSONObject jsonObject, String userId) throws IOException;

    public JSONObject send(Queue queue, JSONObject jsonObject) throws IOException {
        String uuid = generateUUID();
        final JSONObject[] res = new JSONObject[1];
        jsonObject.put("uuid", uuid);

        Connection connection = RMQConnection.getSingleton();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(queue.getRequestQueueName(), true, false, false, null);

        channel.basicPublish("", queue.getRequestQueueName(), null,
                jsonObject.toString().getBytes("UTF-8"));


        channel.queueDeclare(queue.getResponseQueueName(), true, false, false, null);
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                JSONObject responseJSON = new JSONObject(message);
                if (responseJSON.getString("uuid").equals(uuid)) {
                    responseJSON.remove("uuid");
                    res[0] = responseJSON;
                    synchronized (this) {
                        this.notify();
                    }
                }
            }
        };

        channel.basicConsume(queue.getResponseQueueName(), false, consumer);

        while (true) {
            synchronized (consumer) {
                try {
                    consumer.wait();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return res[0];
    }
}
