package http_server.handlers;

import http_server.Server;
import io.netty.channel.*;
import org.json.JSONObject;
import shared.Settings;

import static shared.Helpers.blockAndSubscribe;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {
    private final Settings settings;

    public MQReceiverHandler(Settings settings) {
        super();
        this.settings = settings;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        JSONObject resJSON = blockAndSubscribe(Server.mqSubscriptions, mqPair.uuid, settings.getName(), mqPair.serviceName);
        ctx.fireChannelRead(resJSON);
    }
}
