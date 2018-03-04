package HTTPServer.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import static shared.Helpers.sendJSON;

@ChannelHandler.Sharable
public class JSONSenderHandler extends SimpleChannelInboundHandler<JSONObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JSONObject jsonObject) throws Exception {
        sendJSON(ctx, jsonObject);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
