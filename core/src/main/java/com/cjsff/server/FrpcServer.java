package com.cjsff.server;

import com.cjsff.registry.ServerRegisterDiscovery;
import com.cjsff.registry.ZookeeperService;
import com.cjsff.server.handler.FrpcServerHandler;
import com.cjsff.spi.SerializationSpiManager;
import com.cjsff.transport.codec.PacketCodecHandler;
import com.cjsff.transport.codec.Spliter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rick
 */
public class FrpcServer {

    private static final Logger log = LoggerFactory.getLogger(FrpcServer.class);

    private Map<String, Object> serviceMap = new HashMap<>();


    public void addService(String serviceName, Object o) {
        serviceMap.put(serviceName, o);
    }

    public Map<String,Object> getServiceMap() {
        return serviceMap;
    }

    private FrpcServerOption frpcServerOption = new FrpcServerOption();

    public FrpcServer(int port) throws InterruptedException {
        this(port, null, null);
    }

    public FrpcServer(int port,String zkAddress) throws InterruptedException {
        this(port, zkAddress,null);
    }

    private final EventLoopGroup boss;
    private final EventLoopGroup work;

    public FrpcServer(int port, String zkAddress,FrpcServerOption option) throws InterruptedException {

        if (option != null) {
            BeanCopier copier = BeanCopier.create(FrpcServerOption.class, FrpcServerOption.class, false);
            copier.copy(option, frpcServerOption, null);
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // choose IO model
        if (Epoll.isAvailable()) {
            boss = new EpollEventLoopGroup(1);
            work = new EpollEventLoopGroup(1);
            serverBootstrap.channel(EpollServerSocketChannel.class);
            serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            log.info("use epoll edge trigger model.");
        } else {
            boss = new NioEventLoopGroup(1);
            work = new NioEventLoopGroup(1);
            serverBootstrap.channel(NioServerSocketChannel.class);
            log.info("use normal model.");
        }
        serverBootstrap.group(boss, work);
        // configure TCP related parameters
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);


        serverBootstrap.option(ChannelOption.SO_BACKLOG, frpcServerOption.getBacklog());
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, frpcServerOption.isKeepAlive());
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, frpcServerOption.isTcpNoDelay());
        serverBootstrap.childOption(ChannelOption.SO_LINGER, frpcServerOption.getLinger());
        serverBootstrap.childOption(ChannelOption.SO_SNDBUF, frpcServerOption.getSendBufferSize());
        serverBootstrap.childOption(ChannelOption.SO_RCVBUF, frpcServerOption.getReceiveBufferSize());
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Spliter());
                ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                ch.pipeline().addLast(new FrpcServerHandler());
            }
        });

        serverBootstrap.bind(port).sync().addListener(future -> {
            if (future.isSuccess()) {
                log.info("server bind port is success");
                if (zkAddress != null) {
                    ServerRegisterDiscovery serverRegisterDiscovery = new ZookeeperService(zkAddress);
                    serverRegisterDiscovery.register(port);
                }
            }
        });

        SerializationSpiManager.getInstance().loadSerialization();
    }

    public void stop() {
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
