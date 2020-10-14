package com.cjsff.springboot;


import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcProxy;
import com.cjsff.common.annotation.FrpcServiceConsumer;
import com.cjsff.common.annotation.FrpcServiceProvider;
import com.cjsff.common.annotation.RegistryConstant;
import com.cjsff.server.FrpcServer;
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
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;


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
        Class<?> clazz = frpcServiceProvider.interfaceClass();

        frpcServer.addService(clazz.getName(), bean, frpcProperties.getZookeeperAddress());
      }

    }

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (null == frpcClient) {
      frpcClient = new FrpcClient();
    }

    Class<?> objClz;
    if (AopUtils.isAopProxy(bean)) {
      objClz = AopUtils.getTargetClass(bean);
    } else {
      objClz = bean.getClass();
    }

    Field[] declaredFields = objClz.getDeclaredFields();
    for (Field declaredField : declaredFields) {

      FrpcServiceConsumer frpcServiceConsumer = declaredField.getAnnotation(FrpcServiceConsumer.class);

      if (null != frpcServiceConsumer) {

        String url = frpcServiceConsumer.url();

        if (!StringUtils.isEmpty(url)) {

          String[] split = url.split("://");

          if (RegistryConstant.FRPC.equals(split[0])) {

            frpcClient.initChannelFromServerNodeAddress(split[1], declaredField.getType().getName());

          } else if (RegistryConstant.ZOOKEEPER.equals(split[0])) {

            frpcClient.initChannelFromRegistry(split[1], declaredField.getType().getName());

          } else {
            throw new RuntimeException("get connection type error,not support " + split[0]);
          }

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
