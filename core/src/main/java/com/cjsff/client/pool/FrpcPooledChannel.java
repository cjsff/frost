package com.cjsff.client.pool;

import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcClientOption;
import com.cjsff.registry.RegisterInfo;
import com.cjsff.registry.ServerRegisterDiscovery;
import com.cjsff.registry.ZookeeperService;
import com.cjsff.utils.NetUtils;
import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author cjsff
 */
public class FrpcPooledChannel implements FrpcConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(FrpcPooledChannel.class);

    private GenericObjectPool<Channel> channelGenericObjectPool;

    public FrpcPooledChannel(InetSocketAddress serverAddress, FrpcClient client) {
        this(serverAddress, null, client);
    }

    public FrpcPooledChannel(String zkAddress, FrpcClient client) {
        this(null, zkAddress, client);
    }

    public FrpcPooledChannel(InetSocketAddress serverAddress,String zkAddress, FrpcClient client) {

        FrpcClientOption frpcClientOption = client.getFrpcClientOption();
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        config.setMaxWaitMillis(frpcClientOption.getConnectTimeOutMillis());
        config.setMaxTotal(frpcClientOption.getMaxTotalConnections());
        config.setMaxIdle(frpcClientOption.getMaxTotalConnections());
        config.setMinIdle(frpcClientOption.getMinIdleConnections());
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(frpcClientOption.getTimeBetweenEvictionRunsMillis());

        if (zkAddress != null) {
            ServerRegisterDiscovery serverRegisterDiscovery = new ZookeeperService(zkAddress);
            String serverAddressAndPort = serverRegisterDiscovery.discovery();
            RegisterInfo registerInfo = NetUtils.getRegisterInfo(serverAddressAndPort);

            channelGenericObjectPool = new GenericObjectPool<>(
                    new ChannelPoolFactory(client, registerInfo.getHost(), registerInfo.getPort()), config);
        } else {

            channelGenericObjectPool = new GenericObjectPool<>(
                    new ChannelPoolFactory(client, serverAddress), config
            );
        }

        try {
            channelGenericObjectPool.preparePool();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Channel getChannel() throws Exception {
        return channelGenericObjectPool.borrowObject();
    }

    @Override
    public void returnChannel(Channel channel) {
        channelGenericObjectPool.returnObject(channel);
    }
}
