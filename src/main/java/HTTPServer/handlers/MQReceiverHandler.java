package HTTPServer.handlers;

import HTTPServer.RMQConnection;
import io.netty.channel.*;
import org.json.JSONObject;
import shared.MQSubscriptions.MQSubscriptions;

import static shared.Helpers.blockAndSubscribe;

@ChannelHandler.Sharable
public class MQReceiverHandler extends SimpleChannelInboundHandler<MQHandlerPair> {
    static MQSubscriptions mqSubscriptions = new MQSubscriptions(RMQConnection.getSingleton());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MQHandlerPair mqPair) throws Exception {
        JSONObject resJSON = blockAndSubscribe(mqSubscriptions, mqPair.uuid, "netty", mqPair.serviceName);
        ctx.fireChannelRead(resJSON);
    }


}
