package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.client.pool.FrpcPooledChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author cjsff
 */
public class FrpcClient {

    private static final Logger log = LoggerFactory.getLogger(FrpcClient.class);

    private FrpcClientOption frpcClientOption = new FrpcClientOption();
    private FrpcPooledChannel frpcPooledChannel;
    private Bootstrap bootstrap;

    public FrpcClient(InetSocketAddress serverAddress) {
        this(serverAddress, null, null);
    }

    public FrpcClient(InetSocketAddress serverAddress, FrpcClientOption option) {
        this(serverAddress, option, null);
    }

    public FrpcClient(String zkAddress) {
        this(null, null, zkAddress);
    }

    public FrpcClient(FrpcClientOption option, String zkAddress) {
        this(null, option, zkAddress);
    }


    public FrpcClient(InetSocketAddress serverAddress, FrpcClientOption option, String zkAddress) {

        // 判断直接使用服务端地址，还是从zookeeper中取
        if (serverAddress != null) {
            log.info("serverAddress is :" + serverAddress);
            frpcPooledChannel = new FrpcPooledChannel(serverAddress, this);
        } else {
            frpcPooledChannel = new FrpcPooledChannel(zkAddress, this);
        }

        // 判断用户是否设置自定义客户端相关配置
        if (option != null) {
            BeanCopier copier = BeanCopier.create(FrpcClientOption.class, FrpcClientOption.class, false);
            copier.copy(option, frpcClientOption, null);
        }

        EventLoopGroup work;
        bootstrap = new Bootstrap();
        // 选择IO模型
        if (Epoll.isAvailable()) {
            work = new EpollEventLoopGroup(frpcClientOption.getNettyWorkThreadNum());
            bootstrap.channel(EpollSocketChannel.class);
            log.info("use epoll edge trigger mode");
        } else {
            work = new NioEventLoopGroup(frpcClientOption.getNettyWorkThreadNum());
            bootstrap.channel(NioSocketChannel.class);
            log.info("use normal mode");
        }
        bootstrap.group(work);
        // 配置TPC相关参数
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, frpcClientOption.getConnectTimeOutMillis());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, frpcClientOption.isKeepAlive());
        bootstrap.option(ChannelOption.TCP_NODELAY, frpcClientOption.isNoDelay());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 绑定客户端处理器
                ch.pipeline().addLast(new FrpcClientHandler());
            }
        });

    }

    public Channel getConnect(InetSocketAddress serverAddress) throws InterruptedException {
        return getConnect(null, null, serverAddress);
    }

    public Channel getConnect(String zkAddress, Integer port) throws InterruptedException {
        return getConnect(zkAddress, port, null);
    }

    public Channel getConnect(String zkAddress, Integer port, InetSocketAddress serverAddress) throws InterruptedException {
        if (serverAddress != null) {
            ChannelFuture future = bootstrap.connect(serverAddress).sync();
            return future.channel();
        }
        ChannelFuture future = bootstrap.connect(zkAddress, port).sync();
        return future.channel();
    }

    public FrpcPooledChannel getFrpcPooledChannel() {
        return frpcPooledChannel;
    }

    public FrpcClientOption getFrpcClientOption() {
        return frpcClientOption;
    }

}


