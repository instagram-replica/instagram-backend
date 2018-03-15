package HTTPServer;

import HTTPServer.config.Config;
import HTTPServer.config.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import shared.MQSubscriptions.MQSubscriptions;


public class Server {
    private static final String DEFAULT_CONFIG_URI_LOC = "src/main/java/HTTPServer/config/default_config.json";
    public static Settings settings;
    public static MQSubscriptions mqSubscriptions = new MQSubscriptions(RMQConnection.getSingleton());

    public static void start(Settings settings) {
        Server.settings = settings;

        EventLoopGroup bossGroup = new NioEventLoopGroup(((int) settings.getNumberOfThreads()));
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());

            Channel ch = b.bind((int) settings.getPort()).sync().channel();

            System.out.println("Server is listening on http://127.0.0.1:" + settings.getPort() + '/');

            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        String fileUri = args.length == 1 ? args[0] : DEFAULT_CONFIG_URI_LOC;
        Server.start(Config.getSettings(fileUri));
    }
}
