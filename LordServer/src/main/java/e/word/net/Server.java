package e.word.net;

import e.word.net.server.NettyServer;
import e.word.net.server.NioWebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class Server {
    private final Logger logger = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.Init();
    }
}
