package HTTPServer.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import static shared.Helpers.sendJSON;

@ChannelHandler.Sharable
public class JSONHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        ByteBuf buffer = (ByteBuf) o;
        //TODO: @MAGDY long json gets cut off
        try {
            JSONObject jsonObject = new JSONObject(buffer.toString(CharsetUtil.UTF_8));
            ctx.fireChannelRead(jsonObject);

        } catch (Exception e) {
            sendJSON(ctx, new JSONObject().put("error", "Malformed JSON"));
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
