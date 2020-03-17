package e.word.net;

import e.word.net.server.NettyServer;
import org.apache.log4j.Logger;

/**
 * 服务器界面
 */
public class Server {
    private final Logger logger = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.Init();
    }
}
