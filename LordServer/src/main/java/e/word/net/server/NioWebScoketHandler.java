package e.word.net.server;

import e.word.net.handler.HttpHandler;
import e.word.net.handler.WebSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.apache.log4j.Logger;

public class NioWebScoketHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = Logger.getLogger(NioWebScoketHandler.class);
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("收到消息:" + msg);
        if (msg instanceof FullHttpRequest) {
            //以http请求的形式接入，但是走的是websocket
            HttpHandler.handleHttpRequest(ctx, (FullHttpRequest) msg, handshaker);
        } else if (msg instanceof WebSocketFrame) {
            // 处理websocket客户端的消息
            WebSocketHandler.handlerWebSocketFrame(ctx, (WebSocketFrame) msg, handshaker);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加链接
        logger.debug("客户端加入链接:" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开链接
        logger.debug("客户端退出:" + ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
