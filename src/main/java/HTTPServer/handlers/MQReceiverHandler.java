package HTTPServer.handlers;

import HTTPServer.RMQConnection;
import HTTPServer.Server;
import io.netty.channel.*;
import org.json.JSONObject;
import shared.MQSubscriptions.MQSubscriptions;

import static shared.Helpers.blockAndSubscribe;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        JSONObject resJSON = blockAndSubscribe(Server.mqSubscriptions, mqPair.uuid, Server.settings.getName(), mqPair.serviceName);
        ctx.fireChannelRead(resJSON);
    }
}
