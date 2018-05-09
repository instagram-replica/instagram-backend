package shared;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import shared.mq_server.Controller;
import shared.mq_subscriptions.ExecutionPair;
import shared.mq_subscriptions.MQSubscriptions;

import java.io.IOException;
import java.util.ArrayList;

public class Helpers {
    private static final long TIMEOUT = 4000;

    public static void sendJSON(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {
        ByteBuf content = Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8);
        HttpResponseStatus httpResponseStatus;
        try {
            httpResponseStatus = jsonObject.get("error") == JSONObject.NULL || jsonObject.getJSONObject("error").keySet().size() == 0
                    ? HttpResponseStatus.OK
                    : HttpResponseStatus.INTERNAL_SERVER_ERROR;
        } catch (JSONException e) {
            httpResponseStatus = HttpResponseStatus.OK;
        }

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                httpResponseStatus,
                content
        );
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.close();
    }

    public static JSONObject createJSONError(String message) {
        JSONObject res = new JSONObject();
        res.put("error", message);
        return res;
    }

    public static boolean isAuthorizedToView(String userId, String serviceName, String viewerId, String toBeViewedId) throws IOException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "isUserAuthorizedToView");

        JSONObject paramsObj = new JSONObject();
        paramsObj.put("viewerId", viewerId);
        paramsObj.put("viewedId", toBeViewedId);

        jsonObject.put("params", paramsObj);

        JSONObject authorizationJSONObj = Controller.send(serviceName, "users", jsonObject, userId);

        return authorizationJSONObj.getJSONObject("data").getBoolean("isAuthorizedToView");
    }

    public static String getResponseQueue(String senderServiceName, String receiverServiceName) {
        return senderServiceName + "_" + receiverServiceName;
    }

    public static synchronized JSONObject blockAndSubscribe(MQSubscriptions mqSubscriptions, String uuid, String serviceName, String receiverName) throws IOException, InterruptedException {
        // SUPER DUPER UGLY CODE 🤮

        String queueName = getResponseQueue(serviceName, receiverName);

        final JSONObject[] resJson = {null};

        ExecutionPair pair = new ExecutionPair(uuid, jsonObject -> {
            resJson[0] = jsonObject;
            synchronized (resJson) {
                resJson.notify();
            }
        });


        mqSubscriptions.addListener(queueName, pair);

        synchronized (resJson) {
            while (resJson[0] == null) {
                resJson.wait(TIMEOUT);
                break;
            }
        }
        return resJson[0] != null ? resJson[0] : new JSONObject().put("error", "timeout");
    }


    public static JSONArray getUsersByIds(String serviceName, JSONArray jsonArray, String userId) throws IOException, InterruptedException {
        JSONObject usersJsonObject = new JSONObject().put("ids", jsonArray);
        JSONObject jsonObject = new JSONObject()
                .put("params", usersJsonObject)
                .put("method", "getUsersByIds");
        JSONArray response= new JSONArray();
        JSONArray users =  Controller.send(serviceName, "users", jsonObject, userId).getJSONObject("data").getJSONArray("data");
        for (int i = 0; i <users.length() ; i++) {
            JSONObject temp = new JSONObject();
            temp.put("username",users.getJSONObject(i).get("username"));
            temp.put("userid",users.getJSONObject(i).getString("id"));
            response.put(temp);
        }

        return response;
    }

    public static JSONArray getUsersIdsByUsernames(String serviceName, ArrayList<String> usernames, String userId) throws IOException, InterruptedException {
        JSONObject usersJsonObject = new JSONObject().put("usernames", usernames);
        JSONObject jsonObject = new JSONObject()
                .put("params", usersJsonObject)
                .put("method", "getUsersIdsByUsernames");
        return Controller.send(serviceName, "users", jsonObject, userId).getJSONObject("data").getJSONArray("ids");
    }


}
