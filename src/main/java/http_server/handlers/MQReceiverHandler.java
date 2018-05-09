package http_server.handlers;

import io.netty.channel.*;
import shared.http_server.AMQSubscriptions;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        AMQSubscriptions.addListener(mqPair, ctx);
    }
}
