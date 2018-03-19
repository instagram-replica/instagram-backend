package shared.http_server.handlers;

import http_server.HTTPRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class JSONHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest msg) throws Exception {
        ctx.fireChannelRead(msg.content);
    }
}
