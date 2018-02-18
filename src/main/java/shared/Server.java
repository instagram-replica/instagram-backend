package shared;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;

@ChannelHandler.Sharable
public class Server {
    private int port;
    public Server(String host, int port){ this.port = port; }

    public void run(SimpleChannelInboundHandler handler){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
                                    .allowedRequestHeaders("X-Requested-With", "Content-Type", "Content-Length")
                                    .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS)
                                    .build();
                            ChannelPipeline p = socketChannel.pipeline();

                            p.addLast("decoder", new HttpRequestDecoder());
                            p.addLast("encoder", new HttpResponseEncoder());
                            p.addLast(new CorsHandler(corsConfig));
                            p.addLast(new HTTPHandler());
                            p.addLast(handler);
                        }
                    });
            Channel ch = b.bind(port).sync().channel();
            System.out.println("Server is now running on port " +port);
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
