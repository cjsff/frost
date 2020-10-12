package com.cjsff.server;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rick
 */
@Setter
@Getter
public class FrpcServerOption {

    /**
     * TCP layer keepalive
     */
    private boolean keepAlive = true;
    private boolean tcpNoDelay = true;
    private int linger = 5;
    /**
     * TCP data sending buffer size
     */
    private int sendBufferSize = 1024 * 24;
    /**
     * TCP data receiving buffer size
     */
    private int receiveBufferSize = 1024 * 24;
    private int backlog = 1024;

    private int nettyBossThreadNum = Runtime.getRuntime().availableProcessors() * 2;
    private int nettyWorkThreadNum = Runtime.getRuntime().availableProcessors() * 2;

}
