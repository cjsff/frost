package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.client.pool.FrpcPooledChannel;
import com.cjsff.transport.FrpcRequest;
import com.cjsff.transport.FrpcResponse;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author rick
 */
@SuppressWarnings("unchecked")
public class FrpcProxy implements MethodInterceptor {

    private final FrpcPooledChannel frpcPooledChannel;

    public FrpcProxy(FrpcClient frpcClient) {
        this.frpcPooledChannel = frpcClient.getFrpcPooledChannel();
    }

    public static <T> T getProxy(Class clazz, FrpcClient frpcClient) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new FrpcProxy(frpcClient));
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        // assembly request messages
        String requestId = UUID.randomUUID().toString();
        FrpcRequest request = new FrpcRequest(requestId, method.getDeclaringClass().getName(),
                method.getName(), method.getParameterTypes(), args);

        FrpcClientHandler handler = new FrpcClientHandler();

        // send request to server
        CompletableFuture<FrpcResponse<Object>> frpcFuture = handler.send(request,frpcPooledChannel);
        FrpcResponse<Object> frpcResponse = frpcFuture.get();

        return frpcResponse.getResult();
    }
}
