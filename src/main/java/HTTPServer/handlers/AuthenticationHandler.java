package HTTPServer.handlers;

import HTTPServer.HTTPRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    private final String ACCESS_TOKEN_HEADER = "x-access-token";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest httpRequest) {
        String accessToken = httpRequest.headers.get(ACCESS_TOKEN_HEADER);
        boolean isAuthenticAccessToken = isAuthenticAccessToken(accessToken);
        String userId = decodeAccessToken(accessToken);

        // If access token exists & is inauthentic
        if (accessToken != null && !isAuthenticAccessToken) {
            // TODO: Respond eagerly with error
        }

        HTTPRequest authenticatedHTTPRequest = new HTTPRequest.Builder()
                .method(httpRequest.method)
                .headers(httpRequest.headers)
                .content(httpRequest.content)
                .userId(userId)
                .build();

        ctx.fireChannelRead(authenticatedHTTPRequest);
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

    private boolean isAuthenticAccessToken(String accessToken) {
        // TODO: Check using java-jwt
        return false;
    }

    private String decodeAccessToken(String accessToken) {
        // TODO: Decode using java-jwt
        return "<DUMMY-UUID>";
    }
}
