package http_server.websocket;

import com.rabbitmq.client.*;
import http_server.RMQConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ThreadRegistry {
    private static HashMap<String, ArrayList<ChannelHandlerContext>> threadMap = new HashMap<>();
    private static HashMap<ChannelId, Pair> channelPairs = new HashMap<>();

    public static void register(String threadId, ChannelHandlerContext userContext) throws IOException, TimeoutException {
        // Check if the thread is already created
        // Then add user's context to a list of contexts associated with this threadId
        if (threadMap.get(threadId) != null) {
            threadMap.get(threadId).add(userContext);
        } else {
            // Else create new thread and add user's context
            ArrayList<ChannelHandlerContext> contexts = new ArrayList<>();
            contexts.add(userContext);
            threadMap.put(threadId, contexts);
        }
        // Add a subscriber to listen to all incoming messages in this thread
        Pair subscriptionPair = subscribe(threadId, userContext);
        channelPairs.put(userContext.channel().id(), subscriptionPair);
    }

    private static Pair subscribe(String threadId, ChannelHandlerContext userContext) throws IOException, TimeoutException {
        Connection connection = RMQConnection.getSingleton();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(threadId, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, threadId, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                userContext.channel().write(new TextWebSocketFrame(message));
                userContext.flush();
            }
        };
        String tag = channel.basicConsume(queueName, true, consumer);
        return new Pair(channel, tag);
    }

    public static void unregister(ChannelId channelId) throws IOException {
        if (channelPairs.containsKey(channelId)) {
            Pair subscriptionPair = channelPairs.get(channelId);
            subscriptionPair.getChannel().basicCancel(subscriptionPair.getSubscriptionTag());
            channelPairs.remove(channelId);
        }
    }

    public static void publish(String text, String userId, String threadId) throws IOException, TimeoutException {
        Connection connection = RMQConnection.getSingleton();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(threadId, "fanout");

        channel.basicPublish(threadId, "", null, (new JSONObject().put("userId", userId).put("value", text).toString()).getBytes());

        channel.close();
    }
}
