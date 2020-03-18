package e.word.net.handler;

import com.alibaba.fastjson.JSON;
import e.word.net.buissness.Service;
import e.word.net.model.Event;
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
            Event event = JSON.parseObject(request, Event.class);
            if (event.getType().equals("建立链接")) {
                Service.link(event, ctx.channel().id().asShortText());
            } else if (event.getType().equals("创建房间")) {
                Service.createRoom(event);
            } else if (event.getType().equals("抢地主")) {
                // TODO: 2020/3/17 抢地主
                Service.landLord(event);
            } else if (event.getType().equals("出牌")) {
                // TODO: 2020/3/18 出牌
                Service.play(event);
            }
        } else {
            logger.debug("本例仅支持文本消息，暂不支持二进制消息");
            TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                    + ctx.channel().id() + ":本例仅支持文本消息，暂不支持二进制消息");
            ctx.channel().writeAndFlush(tws);
        }
    }
}
