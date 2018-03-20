package http_server.handlers;

import http_server.HTTPRequest;
import http_server.RMQConnection;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;
import shared.Settings;

import java.util.Properties;
import java.util.UUID;

import static utilities.Main.cloneJSONObject;
import static utilities.Main.readPropertiesFile;

@ChannelHandler.Sharable
public class MQSenderHandler extends SimpleChannelInboundHandler<HTTPRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest httpRequest) throws Exception {

        // TODO: This might be costly, maybe we shouldn't read the file every time a request is received
        Properties props = readPropertiesFile("src/main/resources/requests_mapping.properties");
        JSONObject jsonObject = cloneJSONObject(httpRequest.content);

        Connection connection = RMQConnection.getSingleton();
        Channel channel = connection.createChannel();

        String methodName = jsonObject.getString("method");
        String serviceName = props.getProperty(methodName);


        String uuid = UUID.randomUUID().toString();
        jsonObject
                .put("uuid", uuid)
                .put("sender", Settings.getInstance().getInstanceId())
                .put("userId", httpRequest.userId);

        channel.queueDeclare(serviceName, true, false, false, null);
        channel.basicPublish("", serviceName, null, jsonObject.toString().getBytes("UTF-8"));

        ctx.fireChannelRead(new MQHandlerPair(uuid, serviceName));

        channel.close();

    }
}
