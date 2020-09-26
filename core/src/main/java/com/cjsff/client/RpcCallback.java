package com.cjsff.client;

/**
 * @author cjsff
 */
public interface RpcCallback {

    void success(Object result);

    void fail(Throwable e);

}
