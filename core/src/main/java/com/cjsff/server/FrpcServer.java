package com.cjsff.server;

import com.cjsff.registry.ServerRegisterDiscovery;
import com.cjsff.registry.ZookeeperService;
import com.cjsff.server.handler.FrpcServerHandler;
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

/**
 * @author cjsff
 */
public class FrpcServer {

    private static final Logger log = LoggerFactory.getLogger(FrpcServer.class);

    private FrpcServerOption frpcServerOption = new FrpcServerOption();

    public FrpcServer(int port) throws InterruptedException {
        this(port, null, null);
    }

    public FrpcServer(int port,String zkAddress) throws InterruptedException {
        this(port, zkAddress,null);
    }

    public FrpcServer(int port, String zkAddress,FrpcServerOption option) throws InterruptedException {

        // 判断用户是否设置自定义服务端相关配置
        if (option != null) {
            BeanCopier copier = BeanCopier.create(FrpcServerOption.class, FrpcServerOption.class, false);
            copier.copy(option, frpcServerOption, null);
        }

        EventLoopGroup boss;
        EventLoopGroup work;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 选择IO模型
        if (Epoll.isAvailable()) {
            boss = new EpollEventLoopGroup(frpcServerOption.getNettyBossThreadNum());
            work = new EpollEventLoopGroup(frpcServerOption.getNettyWorkThreadNum());
            serverBootstrap.channel(EpollServerSocketChannel.class);
            serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            log.info("use epoll edge trigger model.");
        } else {
            boss = new NioEventLoopGroup(frpcServerOption.getNettyBossThreadNum());
            work = new NioEventLoopGroup(frpcServerOption.getNettyWorkThreadNum());
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
                // 绑定服务端处理器
                ch.pipeline().addLast(new FrpcServerHandler());
            }
        });

        // 绑定端口
        serverBootstrap.bind(port).sync().addListener(future -> {
            if (future.isSuccess()) {
                log.info("server bind port is success");
                // 端口绑定成功后，判断用户是否要把服务注册到zookeeper
                if (zkAddress != null) {
                    ServerRegisterDiscovery serverRegisterDiscovery = new ZookeeperService(zkAddress);
                    serverRegisterDiscovery.register(port);
                }
            }
        });

    }
}
