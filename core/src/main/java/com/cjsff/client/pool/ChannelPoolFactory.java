package com.cjsff.client.pool;

import com.cjsff.client.FrpcClient;
import io.netty.channel.Channel;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetSocketAddress;

/**
 * @author cjsff
 */
public class ChannelPoolFactory extends BasePooledObjectFactory<Channel> {

    private FrpcClient frpcClient;
    private String zkAddress;
    private Integer port;
    private InetSocketAddress serverAddress;

    public ChannelPoolFactory(FrpcClient frpcClient, InetSocketAddress serverAddress) {
        this(frpcClient, serverAddress, null, null);
    }

    public ChannelPoolFactory(FrpcClient frpcClient, String zkAddress, Integer port) {
        this(frpcClient, null, zkAddress, port);
    }

    public ChannelPoolFactory(FrpcClient frpcClient,InetSocketAddress serverAddress, String zkAddress,Integer port) {
        this.frpcClient = frpcClient;
        this.serverAddress = serverAddress;
        this.zkAddress = zkAddress;
        this.port = port;
    }

    @Override
    public Channel create() throws Exception {
        if (serverAddress != null) {
            return frpcClient.getConnect(serverAddress);
        }
        return frpcClient.getConnect(zkAddress,port);
    }

    @Override
    public PooledObject<Channel> wrap(Channel obj) {
        return new DefaultPooledObject<>(obj);
    }


    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        Channel channel = p.getObject();
        if (channel != null && channel.isOpen() && channel.isActive()) {
            channel.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Channel> p) {
        Channel channel = p.getObject();
        return channel != null && channel.isActive() && channel.isOpen();
    }

}
