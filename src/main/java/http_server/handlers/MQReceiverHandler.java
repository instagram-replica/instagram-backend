package http_server.handlers;

import http_server.Server;
import io.netty.channel.*;
import org.json.JSONObject;
import shared.Settings;

import static shared.Helpers.blockAndSubscribe;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        JSONObject resJSON = blockAndSubscribe(Server.mqSubscriptions, mqPair.uuid, Settings.getInstance().getInstanceId(), mqPair.serviceName);
        ctx.fireChannelRead(resJSON);
    }
}
