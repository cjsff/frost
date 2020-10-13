package com.cjsff.example.springboot.consumer.controller;

import com.cjsff.common.annotation.FrpcServiceConsumer;
import com.cjsff.example.springboot.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rick
 */
@RestController
@Slf4j
public class HelloController {


//  @FrpcServiceConsumer(url = "zookeeper://127.0.0.1:2181")
  @FrpcServiceConsumer(url = "frpc://127.0.0.1:10027")
  private HelloService helloService;


  @RequestMapping("/hello")
  public String sayHello(String name) {
    return helloService.sayHello(name);
  }

}
