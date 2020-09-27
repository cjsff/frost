package com.cjsff.server.handler;

import com.cjsff.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @author cjsff
 */
public class FrpcServerHandler extends SimpleChannelInboundHandler<Object> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    FrpcRequest request = (FrpcRequest) msg;

    // 反射执行请求中要调用的接口
    Object clazz = Class.forName(request.getClassName()).newInstance();
    Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParamTypes());
    Object result = method.invoke(clazz, request.getParams());

    // 把结果组装发送到客户端
    FrpcResponse response = new FrpcResponse();
    response.setId(request.getId());
    response.setResult(result.toString());

    ByteBuf out = ctx.alloc().ioBuffer();
    PacketCodeC instance = PacketCodeC.INSTANCE;
    instance.encode(out, response);
    ctx.channel().writeAndFlush(out);
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
    cause.printStackTrace();
  }
}
