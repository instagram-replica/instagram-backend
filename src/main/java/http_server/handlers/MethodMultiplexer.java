package http_server.handlers;

import http_server.server_initializers.HTTPServerInitializer;
import http_server.server_initializers.WSServerInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.net.URISyntaxException;

public class MethodMultiplexer extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            HttpHeaders headers = httpRequest.headers();

            if (headers.get("Connection").equalsIgnoreCase("Upgrade") || (
                    headers.get("Upgrade") != null &&
                            headers.get("Upgrade").equalsIgnoreCase("WebSocket")
            )) {
                WSServerInitializer.init(ctx.pipeline());
                handleHandshake(ctx, httpRequest);
            } else {
                HTTPServerInitializer.init(ctx.pipeline());
            }
        }
        ctx.fireChannelRead(msg);


    }

    /* Do the handshaking for WebSocket request */
    private void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) throws URISyntaxException {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketURL(req),
                null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }


    private String getWebSocketURL(HttpRequest req) {
        String url = "ws://" + req.headers().get("Host") + req.getUri();
        return url;
    }

}