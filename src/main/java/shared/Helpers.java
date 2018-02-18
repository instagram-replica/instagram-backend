package shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

public class Helpers {
    public static JSONObject getJSONFromByteBuf(ChannelHandlerContext ctx, Object o) {
        return new JSONObject(((ByteBuf) (o)).toString(CharsetUtil.UTF_8));
    }

    public static void sendJSON(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {
        ByteBuf content = Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        channelHandlerContext.write(response);
    }
}
