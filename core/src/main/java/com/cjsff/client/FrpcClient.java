package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.client.pool.FrpcPooledChannel;
import com.cjsff.spi.SerializationSpiManager;
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
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author rick
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

        if (serverAddress != null) {
            log.info("serverAddress is :" + serverAddress);
            frpcPooledChannel = new FrpcPooledChannel(serverAddress, this);
        } else {
            frpcPooledChannel = new FrpcPooledChannel(zkAddress, this);
        }

        if (option != null) {
            BeanCopier copier = BeanCopier.create(FrpcClientOption.class, FrpcClientOption.class, false);
            copier.copy(option, frpcClientOption, null);
        }

        EventLoopGroup work;
        bootstrap = new Bootstrap();

        if (Epoll.isAvailable()) {
            work = new EpollEventLoopGroup(1);
            bootstrap.channel(EpollSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            log.info("use epoll edge trigger mode");
        } else {
            work = new NioEventLoopGroup(1);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            log.info("use normal mode");
        }
        bootstrap.group(work);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, frpcClientOption.getConnectTimeOutMillis());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, frpcClientOption.isKeepAlive());
        bootstrap.option(ChannelOption.TCP_NODELAY, frpcClientOption.isNoDelay());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Spliter());
                ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                ch.pipeline().addLast(new FrpcClientHandler());
            }
        });
        SerializationSpiManager.getInstance().loadSerialization();
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


