package com.cjsff.server;

/**
 * @author cjsff
 */
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


    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getLinger() {
        return linger;
    }

    public void setLinger(int linger) {
        this.linger = linger;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getNettyBossThreadNum() {
        return nettyBossThreadNum;
    }

    public void setNettyBossThreadNum(int nettyBossThreadNum) {
        this.nettyBossThreadNum = nettyBossThreadNum;
    }

    public int getNettyWorkThreadNum() {
        return nettyWorkThreadNum;
    }

    public void setNettyWorkThreadNum(int nettyWorkThreadNum) {
        this.nettyWorkThreadNum = nettyWorkThreadNum;
    }
}
