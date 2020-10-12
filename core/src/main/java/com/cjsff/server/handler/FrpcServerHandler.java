package com.cjsff.server.handler;

import com.cjsff.server.ServiceMap;
import com.cjsff.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author rick
 */
@Slf4j
public class FrpcServerHandler extends SimpleChannelInboundHandler<Object> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    FrpcRequest request = (FrpcRequest) msg;

    ServiceMap serviceMap = ServiceMap.getInstance();
    Map<String, Object> objectMap = serviceMap.getObjectMap();

    Object o = objectMap.get(request.getClassName());

    Object instance = o.getClass().newInstance();
    Method method = instance.getClass().getMethod(request.getMethodName(), request.getParamTypes());
    Object result = method.invoke(instance, request.getParams());

    FrpcResponse<Object> response = new FrpcResponse<>();
    response.setId(request.getId());
    response.setResult(result);
    ByteBuf out = ctx.alloc().ioBuffer();
    PacketCodeC packetCodeC = PacketCodeC.INSTANCE;
    packetCodeC.encode(out, response);
    ctx.channel().writeAndFlush(out);
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
    cause.printStackTrace();
  }
}
