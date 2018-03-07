package HTTPServer.handlers;

import HTTPServer.RMQConnection;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        Connection connection = RMQConnection.getSingleton();
        Channel channel = connection.createChannel();

        final JSONObject[] resJSON = {new JSONObject()};

        channel.queueDeclare(mqPair.queue.getResponseQueueName(), true, false, false, null);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                String message = new String(body, "UTF-8");
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.getString("uuid").equals(mqPair.uuid)) {
                    jsonObject.remove("uuid");
                    try {
                        channel.close();
                        resJSON[0] = jsonObject;
                        synchronized (this) {
                            this.notify();
                        }
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        channel.basicConsume(mqPair.queue.getResponseQueueName(), true, consumer);

        // Block until the consumer responds
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
        ctx.fireChannelRead(resJSON[0]);
    }
}
