package com.cjsff.client;

import lombok.Data;

/**
 * @author rick
 */
@Data
public class FrpcClientOption {

    private int connectTimeOutMillis = 1000;
    private int maxTotalConnections = 8;
    private int minIdleConnections = 8;
    private long timeBetweenEvictionRunsMillis = 5 * 60 * 1000;
    private boolean keepAlive = true;
    private boolean noDelay = true;

    private int nettyWorkThreadNum = Runtime.getRuntime().availableProcessors() * 2;

}
