package HTTPServer.handlers;

import HTTPServer.HTTPRequest;
import HTTPServer.RMQConnection;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import java.util.Properties;
import java.util.UUID;

import static utilities.Main.cloneJSONObject;
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
        String serviceName = props.getProperty(methodName);


        String uuid = UUID.randomUUID().toString();
        jsonObject
                .put("uuid", uuid)
                .put("sender", "netty");


        channel.queueDeclare(serviceName, true, false, false, null);
        channel.basicPublish("", serviceName, null, jsonObject.toString().getBytes("UTF-8"));

        ctx.fireChannelRead(new MQHandlerPair(uuid, serviceName));

        channel.close();

    }
}
