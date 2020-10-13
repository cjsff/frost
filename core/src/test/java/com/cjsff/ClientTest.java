package com.cjsff;

import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcClientOption;
import com.cjsff.client.FrpcProxy;
import com.cjsff.service.SayHelloService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {
  public static void main(String[] args) throws InterruptedException {
    FrpcClientOption frpcClientOption = new FrpcClientOption();
    frpcClientOption.setNettyWorkThreadNum(1);
    FrpcClient client = new FrpcClient();

    client.initChannelFromRegistry("localhost:2181", SayHelloService.class.getName());


    SayHelloService sayHelloService = FrpcProxy.getProxy(SayHelloService.class, client);

    List<String> strings = new ArrayList<>();
    strings.add("cjsff");
    List<String> strings1 = sayHelloService.sayHello(strings);
    for (String s : strings1) {
      System.out.println(s);
    }


  }

}
