package com.cjsff.client.handler;

import com.cjsff.client.FrpcFuture;
import com.cjsff.client.pool.FrpcPooledChannel;
import com.cjsff.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

/**
 * Client request sending, response processing
 * @author rick
 */
public class FrpcClientHandler extends SimpleChannelInboundHandler<Object> {

  private final FrpcFuture frpcFuture;

  public FrpcClientHandler() {
    frpcFuture = FrpcFuture.getInstance();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    FrpcResponse<Object> response = (FrpcResponse<Object>) msg;

    frpcFuture.complete(response);

  }

  public CompletableFuture<FrpcResponse<Object>> send(FrpcRequest request, FrpcPooledChannel frpcPooledChannel) throws Exception {

    // initiate asynchronous request
    CompletableFuture<FrpcResponse<Object>> completableFuture = new CompletableFuture<>();

    // put the asynchronous request into the pending request container
    frpcFuture.put(request.getId(), completableFuture);

    // connection pool to obtain netty channel
    Channel channel = frpcPooledChannel.getChannel();

    ByteBuf buf = channel.alloc().ioBuffer();
    PacketCodeC instance = PacketCodeC.INSTANCE;

    // request data serialization,encoding and writing to ByteBuf
    instance.encode(buf, request);
    if (channel.isActive()) {
      channel.writeAndFlush(buf).addListener((ChannelFutureListener) channelFuture -> {
        if (channelFuture.isSuccess()) {
          // request sent successfully
        } else {

          channelFuture.channel().close();
          completableFuture.completeExceptionally(channelFuture.cause());
        }
      });
    } else {
      throw new RuntimeException("netty channel has bean closed");
    }


    // return netty channel to the connection pool
    frpcPooledChannel.returnChannel(channel);

    // return asynchronous result,wait for server response
    return completableFuture;
  }

}
