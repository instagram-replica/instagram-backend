package shared.http_server;

import shared.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import shared.RMQConnection;

import java.io.IOException;

public class Server {
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    public static void start(ChannelInitializer channelInitializer) {
        bossGroup = new NioEventLoopGroup(Settings.getInstance().getNumberOfThreads());
        workerGroup = new NioEventLoopGroup(Settings.getInstance().getNumberOfThreads());

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);

            Channel ch = b.bind(Settings.getInstance().getPort()).sync().channel();

            System.out.println("Server is listening on http://127.0.0.1:" + Settings.getInstance().getPort() + '/');

            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void close() throws IOException {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        RMQConnection.getSingleton().close();
    }
}
