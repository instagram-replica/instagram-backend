package shared.http_server.handlers;

import http_server.HTTPRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shared.Settings;
import shared.http_server.routes.Controller;

import static shared.Helpers.sendJSON;

public class URIHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    private final Settings settings;

    public URIHandler(Settings settings) {
        super();
        this.settings = settings;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest msg) throws Exception {
        String methodName = msg.uri;
        if (methodName.equals("") || methodName.equals("/")) {
            ctx.fireChannelRead(msg);
        } else {
            sendJSON(ctx, Controller.execute(methodName));
        }
    }
}
