package shared;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

@ChannelHandler.Sharable
public class JSONHandler extends SimpleChannelInboundHandler {

    Controller controller;

    public JSONHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

<<<<<<< HEAD
        JSONObject input = Helpers.getJSONFromByteBuf(ctx, o);
        System.out.println("HHHHHHhhhh " + o.toString());
=======
        openConnection();
        JSONObject input = Helpers.getJSONFromByteBuf(ctx, o);

>>>>>>> d410b630133755453505bc3cb2a6b85353077c66
        JSONObject output = controller.execute(input, "fakejsonid");

        Helpers.sendJSON(ctx, output);
        closeConnection();
    }
}
