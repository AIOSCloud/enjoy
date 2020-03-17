package e.word.net.handler;

import com.alibaba.fastjson.JSON;
import e.word.net.db.Memory;
import e.word.net.model.Room;
import e.word.net.model.User;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

public class HttpHandler {
    private static Logger logger = Logger.getLogger(HttpHandler.class);

    /**
     * 走websocket来
     *
     * @param ctx
     * @param request
     */
    public static void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request, WebSocketServerHandshaker handshaker) {
        logger.debug("收到Http消息......");
        try {
            String path = request.uri();
            String body = request.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = request.method();
            logger.debug("msg:" + body);
            if ("/login".equalsIgnoreCase(path)) {
                // TODO: 2020/3/16 用户登录，用户验证
                //处理登录请求
                logger.info("处理登录请求。。。。。。");
                User user = JSON.parseObject(body, User.class);
                Memory.users.add(user);
                send(body, ctx, HttpResponseStatus.OK);
            } else if ("/room".equalsIgnoreCase(path)) {
                // TODO: 2020/3/16 房间初始化
                //房间初始化
                Room room = new Room();
                //房间洗牌
                room.washCard();
                //房间发牌
                room.dealCard();
                //洗牌
                room.orders();
            } else {
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                        "ws://localhost:8090/websocket", null, false);
                handshaker = wsFactory.newHandshaker(request);
                if (handshaker == null) {
                    WebSocketServerHandshakerFactory
                            .sendUnsupportedVersionResponse(ctx.channel());
                } else {
                    handshaker.handshake(ctx.channel(), request);
                }
            }
        } catch (Exception e) {
            logger.error("HTTP消息处理失败" + e);
        }
    }

    public static void send(String content, ChannelHandlerContext ctx,
                            HttpResponseStatus status) {
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                        Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
