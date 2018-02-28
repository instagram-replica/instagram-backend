package HTTPServer.handlers;

import shared.MQServer.Queue;
import HTTPServer.RMQConnection;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import java.util.Properties;
import java.util.UUID;

import static utilities.Main.readPropertiesFile;

@ChannelHandler.Sharable
public class MQSenderHandler extends SimpleChannelInboundHandler<JSONObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject jsonObject) throws Exception {

        // TODO: This might be costly, maybe we shouldn't read the file every time a request is received
        Properties props = readPropertiesFile("src/main/resources/requests_mapping.properties");

        Connection connection = RMQConnection.getSingleton();
        Channel channel = connection.createChannel();

        String methodName = jsonObject.getString("method");
        String queueName = props.getProperty(methodName);
        Queue queue = new Queue(queueName);

        String uuid = UUID.randomUUID().toString();
        jsonObject.put("uuid", uuid);

        channel.queueDeclare(queue.getRequestQueueName(), true, false, false, null);
        channel.basicPublish("", queue.getRequestQueueName(), null, jsonObject.toString().getBytes("UTF-8"));

        ctx.fireChannelRead(new MQHandlerPair(uuid, queue));

        channel.close();

        //FEDERATEDQS


    }
}
