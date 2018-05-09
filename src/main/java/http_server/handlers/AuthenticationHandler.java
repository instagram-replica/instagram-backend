package http_server.handlers;

import auth.JWTPayload;
import http_server.HTTPRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import static auth.JWT.verifyJWT;
import static shared.Helpers.sendJSON;

@ChannelHandler.Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    private static final String ACCESS_TOKEN_HEADER = "x-access-token";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest httpRequest) throws Exception {
        String accessToken = httpRequest.headers.get(ACCESS_TOKEN_HEADER);
        String method = httpRequest.content.getString("method");
        JWTPayload jwtPayload = null;

        if (accessToken == null /* If token does not exist */) {
            if (!method.equals("signup") && !method.equals("login") /* If method is not signup or login */) {
                sendJSON(ctx, constructMissingTokenErrorResponse());
                return;
            }
        } else /* If token exists */ {
            try {
                jwtPayload = verifyJWT(accessToken);
            } catch (Exception e /* When token is invalid */) {
                sendJSON(ctx, constructInvalidTokenErrorResponse());
                return;
            }
        }

        HTTPRequest authenticatedHTTPRequest = new HTTPRequest.Builder()
                .method(httpRequest.method)
                .headers(httpRequest.headers)
                .content(httpRequest.content)
                .userId(jwtPayload == null ? null : jwtPayload.userId)
                .build();

        ctx.fireChannelRead(authenticatedHTTPRequest);
    }

    private static JSONObject constructMissingTokenErrorResponse() {
        JSONObject error = new JSONObject()
                .put("message", "Unauthenticated request")
                .put(
                        "description",
                        "Missing token. Signup or login first, and then attach the issued token to the "
                                + ACCESS_TOKEN_HEADER
                                + " header in all subsequent requests."
                );
        return new JSONObject()
                .put("data", JSONObject.NULL)
                .put("error", error);
    }

    private static JSONObject constructInvalidTokenErrorResponse() {
        JSONObject error = new JSONObject()
                .put("message", "Unauthenticated request")
                .put(
                        "description",
                        "Invalid token. Signup or login first, and then attach the issued token to the "
                                + ACCESS_TOKEN_HEADER
                                + " header in all subsequent requests."
                );
        return new JSONObject()
                .put("data", JSONObject.NULL)
                .put("error", error);
    }
}
