package shared.http_server;

import com.rabbitmq.client.Channel;
import http_server.handlers.MQHandlerPair;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONObject;
import shared.RMQConnection;
import shared.Settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static shared.Helpers.getResponseQueue;

public class AMQSubscriptions {
    private static final HashMap<String, ConcurrentHashMap<String, ChannelHandlerContext>> map = new HashMap<>();


    public static synchronized void addListener(MQHandlerPair pair, ChannelHandlerContext ctx) throws IOException {
        String serviceName = pair.serviceName;
        if (map.get(serviceName) == null) {
            map.put(serviceName, new ConcurrentHashMap<>());
            initServiceConsumer(serviceName);
        }
        subscribe(pair, ctx);
    }

    private static synchronized void initServiceConsumer(String serviceName) throws IOException {
        Channel channel = RMQConnection.getSingleton().createChannel();

        String queueName = getResponseQueue(Settings.getInstance().getInstanceId(), serviceName);

        channel.queueDeclare(queueName, true, false, false, null);

        channel.basicConsume(queueName, true, (consumerTag, msg) -> {
            String message = new String(msg.getBody(), "UTF-8");
            JSONObject jsonObject = new JSONObject(message);
            String jsonUUID = jsonObject.getString("uuid");
            map.get(serviceName).forEach((uuid, context) -> {
                if (uuid.equals(jsonUUID)) {
                    jsonObject.remove("uuid");
                    context.fireChannelRead(jsonObject);
                }
            });
            map.get(serviceName).remove(jsonUUID);
        }, (consumerTag, sig) -> {
        });
    }

    private static synchronized void subscribe(MQHandlerPair pair, ChannelHandlerContext ctx) {
        map.get(pair.serviceName).put(pair.uuid, ctx);
    }

}