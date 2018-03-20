package shared.mq_server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import json.JSONParser;
import shared.Settings;
import com.rabbitmq.client.*;
import org.json.JSONObject;
import shared.http_server.handlers.HTTPHandler;
import shared.http_server.handlers.JSONHandler;
import shared.http_server.handlers.JSONSenderHandler;
import shared.http_server.handlers.URIHandler;

import java.io.IOException;
import java.util.concurrent.*;

import static shared.Helpers.getResponseQueue;

public class Server {
    public static void run(Controller controller) {
        _run(controller, Settings.getInstance().getNumberOfThreads());
        initHTTPServer();
    }

    private static void initHTTPServer() {
        shared.http_server.Server.start(new ChannelInitializer() {
            @Override
            protected void initChannel(io.netty.channel.Channel ch) throws Exception {
                CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                        .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                        .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS)
                        .build();

                ChannelPipeline p = ch.pipeline();

                p.addLast("codec", new HttpServerCodec());
                p.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));

                p.addLast(new CorsHandler(corsConfig));

                p.addLast(new HTTPHandler());
                p.addLast(new URIHandler());

                p.addLast(new JSONHandler());

                p.addLast(new JSONSenderHandler());

            }
        });
    }

    private static void _run(Controller controller, int numberOfThreads) {
        try {
            Connection connection = RMQConnection.getSingleton();

            final Channel channel = connection.createChannel();
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            channel.queueDeclare(Settings.getInstance().getApplication(), true, false, false, null);
            channel.basicQos(numberOfThreads);

            Consumer consumer = handleDelivery(channel, Settings.getInstance().getApplication(), executor, controller);

            channel.basicConsume(Settings.getInstance().getApplication(), false, consumer);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static DefaultConsumer handleDelivery(Channel channel, String serviceName, ExecutorService executor, Controller controller) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                Runnable task = () -> {
                    try {
                        System.out.println("Start processing the request");
                        String message = new String(body, "UTF-8");

                        JSONObject jsonObject = new JSONObject(message);

                        JSONObject resObj = controller.execute(jsonObject, "");

                        String uuid;
                        try {
                            uuid = jsonObject.getString("uuid");
                            jsonObject.remove("uuid");
                            String queueName = getResponseQueue(JSONParser.getString("sender", jsonObject), serviceName);
                            resObj.put("uuid", uuid);

                            channel.queueDeclare(queueName, true, false, false, null);

                            channel.basicPublish("", queueName, null, resObj.toString().getBytes("UTF-8"));

                        } catch (Exception ignored) {
                        }


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
