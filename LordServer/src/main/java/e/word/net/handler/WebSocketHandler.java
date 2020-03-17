package e.word.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;

import java.util.Date;

public class WebSocketHandler {
    private static Logger logger = Logger.getLogger(WebSocketHandler.class);

    public static void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, WebSocketServerHandshaker handshaker) {
        logger.debug("接收到websocket消息......");
        //判断是否为关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            logger.debug("CloseWebSocketFrame消息");
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
        } else if (frame instanceof PingWebSocketFrame) {
            logger.debug("PingWebSocketFrame消息");
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            logger.debug("服务端收到:" + request);
            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                    + ctx.channel().id() + "：" + request);
            ctx.channel().writeAndFlush(tws);
        } else {
            logger.debug("本例仅支持文本消息，暂不支持二进制消息");
            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                    + ctx.channel().id() + ":本例仅支持文本消息，暂不支持二进制消息");
            ctx.channel().writeAndFlush(tws);
        }
    }
}
