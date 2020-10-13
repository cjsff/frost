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
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rick
 */
public class FrpcClient {

    private static final Logger log = LoggerFactory.getLogger(FrpcClient.class);

    private final FrpcClientOption frpcClientOption = new FrpcClientOption();

    private final Map<String, List<Channel>> serviceNameToChannelListMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap;

    public FrpcClient() {
        this(null);
    }

    public FrpcClient(FrpcClientOption option) {

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
        SpiContainer.getInstance().load(false);
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


    public FrpcClientOption getFrpcClientOption() {
        return frpcClientOption;
    }

    public void initChannelFromServerNodeAddress(String serverNodeAddress,String serviceName) {

        addChannel(serviceName,serverNodeAddress);

    }

    public void initChannelFromRegistry(String registryAddress, String serviceName) {

        ServiceRegisterDiscovery serviceRegisterDiscovery =
                (ServiceRegisterDiscovery) SpiContainer.getInstance().get(ServiceRegisterDiscovery.class.getName());

        serviceRegisterDiscovery.start(registryAddress);

        List<String> discovery = serviceRegisterDiscovery.discovery(serviceName);

        for (String address : discovery) {

           addChannel(serviceName,address);

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

        return loadBalanceStrategy.select(serviceNameToChannelListMap,serviceName);
    }


}


