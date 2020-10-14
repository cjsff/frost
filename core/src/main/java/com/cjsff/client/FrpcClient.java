package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.client.loadbalance.LoadBalanceStrategy;
import com.cjsff.registry.ServiceRegisterDiscovery;
import com.cjsff.spi.SpiContainer;
import com.cjsff.transport.codec.PacketCodecHandler;
import com.cjsff.transport.codec.Spliter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * @author rick
 */
@Slf4j
public class FrpcClient {

  private final Map<String, List<Channel>> serviceNameToChannelListMap = new ConcurrentHashMap<>();

  private final Bootstrap bootstrap;

  public FrpcClient() {

    EventLoopGroup work;
    bootstrap = new Bootstrap();
    ThreadFactory workThreadFactory = new DefaultThreadFactory("netty-client-work-thread", true);
    if (Epoll.isAvailable()) {
      work = new EpollEventLoopGroup(1, workThreadFactory);
      bootstrap.channel(EpollSocketChannel.class);
    } else {
      work = new NioEventLoopGroup(1, workThreadFactory);
      bootstrap.channel(NioSocketChannel.class);
    }
    bootstrap.group(work)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
            .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Spliter());
                ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                ch.pipeline().addLast(new FrpcClientHandler());
              }
            });
    SpiContainer.getInstance().load(false);
  }


  public void initChannelFromServerNodeAddress(String serverNodeAddress, String serviceName) {

    addChannel(serviceName, serverNodeAddress);

  }

  public void initChannelFromRegistry(String registryAddress, String serviceName) {

    ServiceRegisterDiscovery serviceRegisterDiscovery =
            (ServiceRegisterDiscovery) SpiContainer.getInstance().get(ServiceRegisterDiscovery.class.getName());

    serviceRegisterDiscovery.start(registryAddress);

    List<String> discovery = serviceRegisterDiscovery.discovery(serviceName);

    for (String address : discovery) {

      addChannel(serviceName, address);

    }
  }

  private void addChannel(String serviceName, String address) {
    String[] split = address.split(":");

    try {
      ChannelFuture future = bootstrap.connect(split[0], Integer.parseInt(split[1])).sync();

      List<Channel> channelList = serviceNameToChannelListMap.get(serviceName);

      if (CollectionUtils.isEmpty(channelList)) {
        channelList = new ArrayList<>();
      }

      channelList.add(future.channel());

      serviceNameToChannelListMap.put(serviceName, channelList);

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public Channel selectChannel(String serviceName) {

    SpiContainer spiContainer = SpiContainer.INSTANCE;

    LoadBalanceStrategy loadBalanceStrategy = (LoadBalanceStrategy) spiContainer.get(LoadBalanceStrategy.class.getName());

    return loadBalanceStrategy.select(serviceNameToChannelListMap, serviceName);
  }


}


