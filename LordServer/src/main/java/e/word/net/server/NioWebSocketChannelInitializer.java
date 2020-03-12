package e.word.net.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("logging", new LoggingHandler("DEBUG")); //设置log监听器
        channel.pipeline().addLast("http-codec", new HttpServerCodec());// 设置解码器
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));//
        channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        channel.pipeline().addLast("handler", new NioWebScoketHandler());
    }
}
