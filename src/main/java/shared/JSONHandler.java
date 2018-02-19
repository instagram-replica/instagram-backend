package shared;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

@ChannelHandler.Sharable
public class JSONHandler extends SimpleChannelInboundHandler {

    Controller controller;

    public JSONHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        JSONObject input = Helpers.getJSONFromByteBuf(ctx, o);
        JSONObject output = controller.execute(input, "fakejsonid");

        Helpers.sendJSON(ctx, output);
    }
}
