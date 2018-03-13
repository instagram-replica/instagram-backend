package HTTPServer;

import HTTPServer.handlers.*;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;

@ChannelHandler.Sharable
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel arg0) {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                .allowedRequestHeaders("X-Requested-With", "Content-Type","Content-Length")
                .allowedRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE,HttpMethod.OPTIONS)
                .build();

        ChannelPipeline p = arg0.pipeline();

        p.addLast ("codec", new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));

        p.addLast(new CorsHandler(corsConfig));

        p.addLast(new HTTPHandler());
        p.addLast(new AuthenticationHandler());

        p.addLast(new MQSenderHandler());
        p.addLast(new MQReceiverHandler());

        p.addLast(new JSONSenderHandler());

    }
}