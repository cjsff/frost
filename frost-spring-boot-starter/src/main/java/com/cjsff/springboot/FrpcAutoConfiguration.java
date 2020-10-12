package com.cjsff.springboot;


import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcProxy;
import com.cjsff.common.annotation.FrpcServiceConsumer;
import com.cjsff.common.annotation.FrpcServiceProvider;
import com.cjsff.server.FrpcServer;
import com.cjsff.server.ServiceMap;
import com.cjsff.springboot.config.FrpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;


/**
 * @author rick
 */
@Configuration
@EnableConfigurationProperties(FrpcProperties.class)
@ConditionalOnBean(annotation = EnableFrpcConfiguration.class)
@Slf4j
public class FrpcAutoConfiguration implements BeanPostProcessor {

  private FrpcClient frpcClient;
  private FrpcServer frpcServer;

  @Autowired
  private FrpcProperties frpcProperties;


  @Bean
  public FrpcAutoConfiguration FrpcAutoConfiguration() throws InterruptedException {
    InetSocketAddress serverAddress = new InetSocketAddress(frpcProperties.getIp(), frpcProperties.getPort());
    if (frpcProperties.isClient()) {

      this.frpcClient = new FrpcClient(serverAddress);
    }
    if (frpcProperties.isServer()) {
      this.frpcServer = new FrpcServer(frpcProperties.getPort());
    }
    return new FrpcAutoConfiguration();
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean.getClass().isAnnotationPresent(FrpcServiceProvider.class)) {
      if (null == frpcServer) {
        return null;
      }
      FrpcServiceProvider frpcServiceProvider = bean.getClass().getAnnotation(FrpcServiceProvider.class);

      if (null != frpcServiceProvider) {
        Class<?> aClass = frpcServiceProvider.interfaceClass();
        ServiceMap serviceMap = ServiceMap.getInstance();
        serviceMap.put(aClass.getName(),bean);
      }

    }

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

    Class<?> objClz;
    if (AopUtils.isAopProxy(bean)) {
      objClz = AopUtils.getTargetClass(bean);
    } else {
      objClz = bean.getClass();
    }

    Field[] declaredFields = objClz.getDeclaredFields();
    for (Field declaredField : declaredFields) {

      FrpcServiceConsumer rpcReference = declaredField.getAnnotation(FrpcServiceConsumer.class);

      if (null != rpcReference) {
        if (null == frpcClient) {
          return bean;
        }
        Object proxy = FrpcProxy.getProxy(declaredField.getType(), frpcClient);
        declaredField.setAccessible(true);
        try {
          declaredField.set(bean, proxy);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }

    }
    return bean;
  }


}
