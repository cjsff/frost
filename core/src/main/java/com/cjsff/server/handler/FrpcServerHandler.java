package com.cjsff.server.handler;

import com.cjsff.transport.*;
import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author cjsff
 */
public class FrpcServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(FrpcServerHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        FrpcRequest request = (FrpcRequest) msg;
//            byte[] bytes = new byte[len];
//            buf.readBytes(bytes);
//            log.info("request messages is : {}", Arrays.toString(bytes));
//
//            // 反序列化
//            Serialization json = new JsonSerializer();
//            FrpcRequest request = json.deserialize(bytes, FrpcRequest.class);

        // 反射执行请求中要调用的接口
        Object clazz = Class.forName(request.getClassName()).newInstance();
        Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        Object result = method.invoke(clazz, request.getParams());

        // 把结果组装发送到客户端
        FrpcResponse response = new FrpcResponse();
        response.setId(request.getId());
        response.setResult(result.toString());

//            byte[] responseByte = json.serialize(response);
        ByteBuf out = ctx.alloc().ioBuffer();
        PacketCodeC instance = PacketCodeC.INSTANCE;
        instance.encode(out,response);
        ctx.channel().writeAndFlush(out);
        System.out.println("result=" + result + ",requestId=" + request.getId());
//        ByteBuf buf = (ByteBuf) msg;
//        int len = buf.readableBytes();
//
//        if (len > 0) {
////            PacketCodeC packetCodeC = PacketCodeC.INSTANCE;
//
//
//        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
