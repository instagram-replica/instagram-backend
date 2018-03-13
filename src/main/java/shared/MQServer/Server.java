package shared.MQServer;

import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.*;

import static shared.Helpers.getResponseQueue;

public class Server {
    String serviceName;

    public Server(String serviceName) {
        this.serviceName = serviceName;
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
            channel.queueDeclare(serviceName, true, false, false, null);
            channel.basicQos(numberOfThreads);

            Consumer consumer = handleDelivery(channel, serviceName, executor, controller);

            channel.basicConsume(serviceName, false, consumer);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static DefaultConsumer handleDelivery(Channel channel, String serviceName, ExecutorService executor, Controller controller) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Runnable task = () -> {
                    try {
                        System.out.println("Start processing the request");
                        String message = new String(body, "UTF-8");

                        JSONObject jsonObject = new JSONObject(message);

                        String queueName = getResponseQueue(jsonObject.getString("sender"), serviceName);

                        String uuid = jsonObject.getString("uuid");
                        jsonObject.remove("uuid");

                        JSONObject resObj = controller.execute(jsonObject, "");
                        resObj.put("uuid", uuid);

                        channel.queueDeclare(queueName, true, false, false, null);

                        channel.basicPublish("", queueName, null, resObj.toString().getBytes("UTF-8"));

                    } catch (Exception e) {
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
