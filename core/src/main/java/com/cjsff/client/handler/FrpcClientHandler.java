package com.cjsff.client.handler;

import com.cjsff.client.FrpcFuture;
import com.cjsff.client.pool.FrpcPooledChannel;
import com.cjsff.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cjsff
 */
public class FrpcClientHandler extends SimpleChannelInboundHandler<Object> {

  private static ConcurrentHashMap<String, FrpcFuture> pendingRpc = new ConcurrentHashMap<>();

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    FrpcResponse response = (FrpcResponse) msg;

    // 拿出请求id,查询ConcurrentHashMap中是否存在这条请求
    String requestId = response.getId();
    FrpcFuture frpcFuture = pendingRpc.get(requestId);
    if (frpcFuture != null) {
      pendingRpc.remove(requestId);
      // 把响应装进异步请求
      frpcFuture.done(response);
    }

  }

  public FrpcFuture send(FrpcRequest request, FrpcPooledChannel frpcPooledChannel) throws Exception {

    // 组装异步请求
    FrpcFuture frpcFuture = new FrpcFuture(request);

    // 把异步请求装进ConcurrentHashMap
    pendingRpc.put(request.getId(), frpcFuture);

    // 连接池获取Channel
    Channel channel = frpcPooledChannel.getChannel();

    // 初始化ByteBuf并写进请求数据发送到服务端
    ByteBuf buf = channel.alloc().ioBuffer();
    PacketCodeC instance = PacketCodeC.INSTANCE;
    instance.encode(buf, request);
    channel.writeAndFlush(buf);

    // 返回Channel到连接池
    frpcPooledChannel.returnChannel(channel);

    // 返回异步请求, 服务端返回之前一直阻塞等待结果
    return frpcFuture;
  }

}
