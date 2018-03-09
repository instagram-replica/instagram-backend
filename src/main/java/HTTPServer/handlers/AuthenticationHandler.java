package HTTPServer.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;

@ChannelHandler.Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private boolean isAuthenticAccessToken(String accessToken) {
        // TODO: Check using java-jwt
        return true;
    }

    private String decodeAccessToken(String accessToken) {
        // TODO: Decode using java-jwt
        return "<DUMMY-UUID>";
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        // TODO: Replace HashMap with custom RequestData class
        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("fullHttpRequest", fullHttpRequest);

        // TODO: Issue access token if sign in command
        String ACCESS_TOKEN_HEADER = "x-access-token";
        String accessToken = fullHttpRequest.headers().get(ACCESS_TOKEN_HEADER);
        boolean isAuthenticAccessToken = isAuthenticAccessToken(accessToken);
        String userId = decodeAccessToken(accessToken);

        if (accessToken == null /* Access token does not exist */) {
            requestData.put("isUserAuthenticted", false);
        } else {
            if (isAuthenticAccessToken /* Access token exists & is authentic */) {
                requestData.put("isUserAuthenticted", true);
                requestData.put("userId", userId);
            } else /* Access token exists & is inauthentic */ {
                // TODO: Respond eagerly with error
            }
        }

        // TODO: Adapt handlers down the stream with the requestData argument
        ctx.fireChannelRead(requestData);
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
