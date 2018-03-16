package shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import shared.MQServer.Controller;
import shared.MQSubscriptions.ExecutionPair;
import shared.MQSubscriptions.MQSubscriptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Helpers {
    public static JSONObject getJSONFromByteBuf(ChannelHandlerContext ctx, Object o) {
        return new JSONObject(((ByteBuf) (o)).toString(CharsetUtil.UTF_8));
    }

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

    public static JSONArray getUsersByIds(String serviceName, JSONArray jsonArray) throws IOException, InterruptedException {
        JSONObject usersJsonObject = new JSONObject().put("ids", jsonArray);
        JSONObject jsonObject = new JSONObject()
                .put("params", usersJsonObject)
                .put("method", "getUsersByIds");
        return Controller.send(serviceName, "users", jsonObject).getJSONObject("data").getJSONArray("users");
    }

    public static JSONArray getUsersIdsByUsernames(String serviceName, ArrayList<String> usernames) throws IOException, InterruptedException {
        JSONObject usersJsonObject = new JSONObject().put("usernames", usernames);
        JSONObject jsonObject = new JSONObject()
                .put("params", usersJsonObject)
                .put("method", "getUsersIdsByUsernames");
        return Controller.send(serviceName, "users", jsonObject).getJSONObject("data").getJSONArray("ids");
    }


}
