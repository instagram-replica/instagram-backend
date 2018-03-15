package http_server.handlers;

import http_server.HTTPRequest;
import auth.JWTPayload;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static auth.JWT.verifyJWT;

@ChannelHandler.Sharable
public class AuthenticationHandler extends SimpleChannelInboundHandler<HTTPRequest> {
    private static final String ACCESS_TOKEN_HEADER = "x-access-token";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HTTPRequest httpRequest) {
        String accessToken = httpRequest.headers.get(ACCESS_TOKEN_HEADER);
        JWTPayload jwtPayload = null;

        if (accessToken != null /* If token exists */) {
            try {
                jwtPayload = verifyJWT(accessToken);
            } catch (Exception e /* When token is invalid */) {
                // TODO: Respond eagerly with error
            }
        }

        HTTPRequest authenticatedHTTPRequest = new HTTPRequest.Builder()
                .method(httpRequest.method)
                .headers(httpRequest.headers)
                .content(httpRequest.content)
                .userId(jwtPayload == null? null : jwtPayload.userId)
                .build();

        ctx.fireChannelRead(authenticatedHTTPRequest);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
