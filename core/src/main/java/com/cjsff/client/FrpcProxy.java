package com.cjsff.client;

import com.cjsff.client.handler.FrpcClientHandler;
import com.cjsff.client.pool.FrpcPooledChannel;
import com.cjsff.transport.FrpcRequest;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author cjsff
 */
@SuppressWarnings("unchecked")
public class FrpcProxy implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FrpcProxy.class);

    private FrpcPooledChannel frpcPooledChannel;

    public FrpcProxy(FrpcClient frpcClient) {
        // 用客户端拿到连接池对象
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

        // 请求消息组装
        String requestId = UUID.randomUUID().toString();
        FrpcRequest request = new FrpcRequest(requestId, method.getDeclaringClass().getName(),
                method.getName(), method.getParameterTypes(), args);
        log.debug("requestId is : {}", requestId);
        log.debug("className is : {}",request.getClassName());
        log.debug("methodName is : {}",request.getMethodName());
        Arrays.stream(request.getParams()).forEach(param -> log.debug("param is : {}",param));
        Arrays.stream(request.getParamTypes()).forEach(paramType -> log.debug("param type is : {}",paramType));

        FrpcClientHandler handler = new FrpcClientHandler();
        // 客户端请求服务端
        FrpcFuture frpcFuture = handler.send(request,frpcPooledChannel);
        Object o = frpcFuture.get();
        System.out.println("result=" + o + ",requestId=" + requestId);
        return o;
    }
}
