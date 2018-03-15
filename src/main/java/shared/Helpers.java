package shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import shared.mq_server.Controller;
import shared.mq_subscriptions.ExecutionPair;
import shared.mq_subscriptions.MQSubscriptions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Helpers {
    public static void sendJSON(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {
        ByteBuf content = Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        channelHandlerContext.writeAndFlush(response);
    }

    public static JSONObject createJSONError(String message) {
        JSONObject res = new JSONObject();
        res.put("error", message);
        return res;
    }

    public static boolean isAuthorizedToView(String serviceName, String viewerId, String toBeViewedId) throws IOException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "authorizedToView");

        JSONObject paramsObj = new JSONObject();
        paramsObj.put("viewerId", viewerId);
        paramsObj.put("toBeViewedId", toBeViewedId);

        jsonObject.put("params", paramsObj);

        JSONObject authorizationJSONObj = Controller.send(serviceName, "users", jsonObject);
        return authorizationJSONObj.getBoolean("authorized");
    }

    public static String getResponseQueue(String senderServiceName, String receiverServiceName) {
        return senderServiceName + "_" + receiverServiceName;
    }

    public static synchronized JSONObject blockAndSubscribe(MQSubscriptions mqSubscriptions, String uuid, String serviceName, String receiverName) throws IOException, InterruptedException {
        String queueName = getResponseQueue(serviceName, receiverName);

        final AtomicReference<JSONObject> resJson = new AtomicReference<>();

        ExecutionPair pair = new ExecutionPair(uuid, jsonObject -> {
            resJson.set(jsonObject);
            synchronized (resJson) {
                resJson.notify();
            }
        });


        mqSubscriptions.addListener(queueName, pair);

        synchronized (resJson) {
            while (resJson.get() == null) {
                resJson.wait();
                break;
            }
        }
        return resJson.get();
    }
}
