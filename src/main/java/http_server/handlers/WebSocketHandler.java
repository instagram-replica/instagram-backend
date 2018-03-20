package http_server.handlers;

import http_server.websocket.ThreadRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.json.JSONObject;

@ChannelHandler.Sharable
public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame) {
                JSONObject jsonObject = new JSONObject(((TextWebSocketFrame) msg).text());
                JSONObject paramsObj = jsonObject.getJSONObject("params");
                String method = jsonObject.getString("method");

                String userId = paramsObj.getString("userId");
                String threadId = paramsObj.getString("threadId");
                if (method.equals("auth")) {
                    ThreadRegistry.register(threadId, ctx);
                } else if (method.equals("send")) {
                    String value = paramsObj.getString("value");
                    ThreadRegistry.publish(value, userId, threadId);
                }
            }
            if (msg instanceof CloseWebSocketFrame) {
                ThreadRegistry.unregister(ctx.channel().id());
            }
        }
    }
}