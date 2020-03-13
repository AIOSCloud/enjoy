package e.word.net.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import e.word.net.model.Message;
import e.word.net.model.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

public class NioWebScoketHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = Logger.getLogger(NioWebScoketHandler.class);
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("收到消息");
        if (msg instanceof FullHttpRequest) {
            //以http请求的形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof
                WebSocketFrame) {
            // 处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加链接
        logger.debug("客户端加入链接:" + ctx.channel());
        System.out.println("客户端加入链接:\" + ctx.channel()");
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

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否为关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        }
        //判断是否为ping 消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        //本例教程仅支持文本消息，不支持而二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.debug("本例仅支持文本消息，暂不支持二进制消息");
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()));
        } else {
            String request = ((TextWebSocketFrame) frame).text();
            logger.debug("服务端收到:" + request);
            // TODO: 2020/3/12  对客户端收到的消息进行处理
            Message message = JSON.parseObject(request, new TypeReference<Message>() {
            }.getType());
            //斗地主
            if (message.getMainType() == 1 && message.getExtType() == 0) {
                // TODO: 2020/3/12  用户登录
                User user = message.getUser();
                if (StringUtils.isNotEmpty(user.getUserName())) {
                    // TODO: 2020/3/12 用户信息校验
                }
            }
        }
    }

    //唯一的一次HTTP请求
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        logger.debug("收到Http消息");
        try {
            String path = request.uri();
            String body = request.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = request.method();
            if ("/login".equalsIgnoreCase(path)) {
                //处理登录请求
                logger.info("处理登录请求。。。。。。");
                send(body, ctx, HttpResponseStatus.OK);
            }
        } catch (Exception e) {
            logger.error("HTTP消息处理失败" + e);
        }
    }

    private void send(String content, ChannelHandlerContext ctx,
                      HttpResponseStatus status) {
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                        Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
