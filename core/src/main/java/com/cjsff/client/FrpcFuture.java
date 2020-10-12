package com.cjsff.client;


import com.cjsff.transport.FrpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rick
 */
public class FrpcFuture {

  private static final FrpcFuture FRPC_FUTURE = new FrpcFuture();

  private FrpcFuture() {

  }

  public static FrpcFuture getInstance() {
    return FRPC_FUTURE;
  }

  private static final Map<String, CompletableFuture<FrpcResponse<Object>>> PENDING_RPC
          = new ConcurrentHashMap<>();

  public void put(String requestId, CompletableFuture<FrpcResponse<Object>> future) {
    PENDING_RPC.put(requestId, future);
  }

  public void complete(FrpcResponse<Object> response) {
    CompletableFuture<FrpcResponse<Object>> future = PENDING_RPC.remove(response.getId());
    if (null != future) {
      future.complete(response);
    } else {
      throw new IllegalStateException();
    }
  }
}
