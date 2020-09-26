package com.cjsff.client;

/**
 * @author cjsff
 */
public class FrpcClientOption {

    private int connectTimeOutMillis = 1000;
    private int maxTotalConnections = 8;
    private int minIdleConnections = 8;
    private long timeBetweenEvictionRunsMillis = 5 * 60 * 1000;
    private boolean keepAlive = true;
    private boolean noDelay = true;

    private int nettyWorkThreadNum = Runtime.getRuntime().availableProcessors() * 2;

    public FrpcClientOption() {
    }

    public FrpcClientOption(int connectTimeOutMillis, int maxTotalConnections, int minIdleConnections, long timeBetweenEvictionRunsMillis, boolean keepAlive, boolean noDelay, int nettyWorkThreadNum) {
        this.connectTimeOutMillis = connectTimeOutMillis;
        this.maxTotalConnections = maxTotalConnections;
        this.minIdleConnections = minIdleConnections;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.keepAlive = keepAlive;
        this.noDelay = noDelay;
        this.nettyWorkThreadNum = nettyWorkThreadNum;
    }

    public int getConnectTimeOutMillis() {
        return connectTimeOutMillis;
    }

    public void setConnectTimeOutMillis(int connectTimeOutMillis) {
        this.connectTimeOutMillis = connectTimeOutMillis;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public int getMinIdleConnections() {
        return minIdleConnections;
    }

    public void setMinIdleConnections(int minIdleConnections) {
        this.minIdleConnections = minIdleConnections;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isNoDelay() {
        return noDelay;
    }

    public void setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
    }

    public int getNettyWorkThreadNum() {
        return nettyWorkThreadNum;
    }

    public void setNettyWorkThreadNum(int nettyWorkThreadNum) {
        this.nettyWorkThreadNum = nettyWorkThreadNum;
    }
}
