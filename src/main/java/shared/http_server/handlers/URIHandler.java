package shared.http_server.handlers;

import http_server.HTTPRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import shared.Settings;

import static shared.Helpers.sendJSON;

public class URIHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    private final Settings settings;

    public URIHandler(Settings settings) {
        super();
        this.settings = settings;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest msg) throws Exception {
        if (msg.uri.equals("/info")) {
            sendJSON(ctx, settings.toJSON());
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
