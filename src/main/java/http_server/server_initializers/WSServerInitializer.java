package http_server.server_initializers;

import http_server.handlers.WebSocketHandler;
import io.netty.channel.ChannelPipeline;

public class WSServerInitializer {
    public static void init(ChannelPipeline p) {
        p.addLast(new WebSocketHandler());
    }
}
