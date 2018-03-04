package HTTPServer.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

@ChannelHandler.Sharable
public class JSONHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        ByteBuf buffer = (ByteBuf) o;
        JSONObject jsonObject = new JSONObject(buffer.toString(CharsetUtil.UTF_8));
        ctx.fireChannelRead(jsonObject);
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
