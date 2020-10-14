package com.cjsff.server;

import com.cjsff.registry.ServiceRegisterDiscovery;
import com.cjsff.server.handler.FrpcServerHandler;
import com.cjsff.spi.SpiContainer;
import com.cjsff.transport.codec.PacketCodecHandler;
import com.cjsff.transport.codec.Spliter;
import com.cjsff.utils.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author rick
 */
public class FrpcServer {

  private static final Logger log = LoggerFactory.getLogger(FrpcServer.class);

  private final int port;

  private ServiceRegisterDiscovery serviceRegisterDiscovery;

  public void addService(String serviceName, Object o, String registryAddress) {

    ServiceMap serviceMap = ServiceMap.getInstance();
    serviceMap.put(serviceName, o);

    if (null == serviceRegisterDiscovery && null != registryAddress) {
      serviceRegisterDiscovery = (ServiceRegisterDiscovery) SpiContainer.getInstance()
              .get(ServiceRegisterDiscovery.class.getName());

      serviceRegisterDiscovery.start(registryAddress);
      serviceRegisterDiscovery.registered(serviceName, NetUtils.getHostAddress(), port);
    }

  }


  private final EventLoopGroup boss;
  private final EventLoopGroup work;

  public FrpcServer(int port) throws InterruptedException {

    this.port = port;

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    ThreadFactory bossThreadFactory = new DefaultThreadFactory("netty-server-boss-thread", true);
    ThreadFactory workThreadFactory = new DefaultThreadFactory("netty-server-work-thread", true);
    // choose IO model
    if (Epoll.isAvailable()) {
      boss = new EpollEventLoopGroup(1, bossThreadFactory);
      work = new EpollEventLoopGroup(1, workThreadFactory);
      serverBootstrap.channel(EpollServerSocketChannel.class);
      serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
      serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
      log.info("use epoll edge trigger model.");
    } else {
      boss = new NioEventLoopGroup(1, bossThreadFactory);
      work = new NioEventLoopGroup(1, workThreadFactory);
      serverBootstrap.channel(NioServerSocketChannel.class);
      log.info("use normal model.");
    }
    serverBootstrap.group(boss, work)
            .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
            .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
            .childHandler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Spliter());
                ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                ch.pipeline().addLast(new FrpcServerHandler());
              }
            });

    serverBootstrap.bind(port).sync().addListener(future -> {
      if (future.isSuccess()) {
        log.info("server bind port:{} is success", port);
      }
    });

    SpiContainer.getInstance().load(false);
  }

}
