package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.transport.FrpcRequest;
import com.cjsff.transport.FrpcResponse;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author rick
 */
@SuppressWarnings("unchecked")
public class FrpcProxy implements MethodInterceptor {

    private  FrpcClient frpcClient;

    public FrpcProxy(FrpcClient frpcClient) {
        this.frpcClient =frpcClient;
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

        Channel channel = frpcClient.selectChannel(method.getDeclaringClass().getName());

        // send request to server
        CompletableFuture<FrpcResponse<Object>> frpcFuture = handler.send(request,channel);
        FrpcResponse<Object> frpcResponse = frpcFuture.get();

        return frpcResponse.getResult();
    }
}
