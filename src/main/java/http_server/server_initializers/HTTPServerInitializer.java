package http_server.server_initializers;

import http_server.handlers.AuthenticationHandler;
import http_server.handlers.MQReceiverHandler;
import http_server.handlers.MQSenderHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.cors.CorsHandler;
import shared.http_server.handlers.HTTPHandler;
import shared.http_server.handlers.JSONSenderHandler;
import shared.http_server.handlers.URIHandler;

public class HTTPServerInitializer {
    public static void init(ChannelPipeline p) {
        p.addLast(new HTTPHandler());
        p.addLast(new URIHandler());

        p.addLast(new AuthenticationHandler());

        p.addLast(new MQSenderHandler());
        p.addLast(new MQReceiverHandler());

        p.addLast(new JSONSenderHandler());
    }
}
