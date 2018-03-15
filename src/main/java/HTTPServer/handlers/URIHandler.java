package HTTPServer.handlers;

import HTTPServer.HTTPRequest;
import HTTPServer.Server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static shared.Helpers.sendJSON;

public class URIHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest msg) throws Exception {
        if (msg.uri.equals("/info")) {
            sendJSON(ctx, Server.settings.toJSON());
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
