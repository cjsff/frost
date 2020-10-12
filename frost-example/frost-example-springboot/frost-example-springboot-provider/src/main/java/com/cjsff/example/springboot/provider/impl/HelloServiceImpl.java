package com.cjsff.example.springboot.provider.impl;

import com.cjsff.common.annotation.FrpcServiceProvider;
import com.cjsff.example.springboot.api.HelloService;
import org.springframework.stereotype.Component;

/**
 * @author rick
 */
@FrpcServiceProvider(interfaceClass = HelloService.class)
@Component
public class HelloServiceImpl implements HelloService {


  @Override
  public String sayHello(String name) {
    return "hello," + name;
  }
}
