package com.cjsff.client.pool;

import io.netty.channel.Channel;

/**
 * @author cjsff
 */
public interface FrpcConnectionPool {

    Channel getChannel() throws Exception;

    void returnChannel(Channel channel);

}
