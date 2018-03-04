package HTTPServer;

import HTTPServer.handlers.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel arg0) {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowedRequestHeaders("X-Requested-With", "Content-Type","Content-Length")
                .allowedRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();

        ChannelPipeline p = arg0.pipeline();


        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());

        p.addLast(new CorsHandler(corsConfig));

        p.addLast(new HTTPHandler());
        p.addLast(new JSONHandler());

        p.addLast(new MQSenderHandler());
        p.addLast(new MQReceiverHandler());

        p.addLast(new JSONSenderHandler());

    }
}
