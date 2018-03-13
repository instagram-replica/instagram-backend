package HTTPServer.handlers;

import HTTPServer.HTTPRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class HTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        HTTPRequest httpRequest = new HTTPRequest.Builder()
                .method(decodeHTTPMethod(fullHttpRequest))
                .headers(decodeHTTPHeaders(fullHttpRequest))
                .content(decodeHTTPContent(fullHttpRequest))
                .build();

        ctx.fireChannelRead(httpRequest);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static String decodeHTTPMethod(FullHttpRequest fullHttpRequest) {
        return fullHttpRequest.method().name();
    }

    private static HashMap<String, String> decodeHTTPHeaders(FullHttpRequest fullHttpRequest) {
        HashMap<String, String> headers = new HashMap<>();

        for (Map.Entry<String, String> entry : fullHttpRequest.headers().entries()) {
            headers.put(entry.getKey(), entry.getValue());
        }

        return headers;
    }

    private static JSONObject decodeHTTPContent(FullHttpRequest fullHttpRequest) {
        return new JSONObject(fullHttpRequest.content().toString(CharsetUtil.UTF_8));
    }
}
