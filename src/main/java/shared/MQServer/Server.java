package shared.MQServer;

import com.rabbitmq.client.*;
import org.json.JSONObject;
import shared.Controller;

import java.io.IOException;
import java.util.concurrent.*;

public class Server {
    Queue queue;

    public Server(String queueName) throws IOException {
        this.queue = new Queue(queueName);
    }

    public void run(Controller controller) {
        int numberOfThreads = 100;
        _run(controller, numberOfThreads);
    }

    public void run(Controller controller, int numberOfThreads) {
        _run(controller, numberOfThreads);
    }

    private void _run(Controller controller, int numberOfThreads) {
        try {
            Connection connection = RMQConnection.getSingleton();
            final Channel channel = connection.createChannel();
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            //TODO: Research these boolean variables
            channel.queueDeclare(queue.getRequestQueueName(), true, false, false, null);
            channel.basicQos(numberOfThreads);

            Consumer consumer = handleDelivery(channel, queue, executor, controller);

            channel.basicConsume(queue.getRequestQueueName(), false, consumer);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static DefaultConsumer handleDelivery(Channel channel, Queue queue, ExecutorService executor, Controller controller) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Runnable task = () -> {
                    try {
                        System.out.println("Start processing the request");

                        String message = new String(body, "UTF-8");

                        JSONObject jsonObject = new JSONObject(message);
                        String uuid = jsonObject.getString("uuid");
                        jsonObject.remove("uuid");

                        JSONObject resObj = controller.execute(jsonObject, "");
                        resObj.put("uuid", uuid);

                        channel.queueDeclare(queue.getResponseQueueName(), true, false, false, null);

                        channel.basicPublish("", queue.getResponseQueueName(), null, resObj.toString().getBytes("UTF-8"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            System.out.println("Done processing the request");
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                executor.execute(task);

            }
        };
    }

}
