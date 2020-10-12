package com.cjsff;

import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcClientOption;
import com.cjsff.client.FrpcProxy;
import com.cjsff.service.SayHelloService;
import com.cjsff.service.impl.SayHelloServiceImpl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ClientTest {
  public static void main(String[] args) throws InterruptedException {
    InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1",10027);
    FrpcClientOption frpcClientOption = new FrpcClientOption();
    frpcClientOption.setNettyWorkThreadNum(1);
    FrpcClient client = new FrpcClient(serverAddress,frpcClientOption);
    SayHelloService sayHelloService = FrpcProxy.getProxy(SayHelloServiceImpl.class, client);

    List<String> strings = new ArrayList<>();
    strings.add("cjsff");
    List<String> strings1 = sayHelloService.sayHello(strings);
    for (String s : strings1) {
      System.out.println(s);
    }
//    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 50, 60L,
//            TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000), Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.AbortPolicy());
//
//    for (int i = 0; i < 10000; i++) {
//      threadPoolExecutor.execute(new ThreadTask(sayHelloService));
//      System.out.println(i);
//    }


  }


  public static class ThreadTask implements Runnable {

    private SayHelloService sayHelloService;

    public ThreadTask(SayHelloService sayHelloService) {
      this.sayHelloService = sayHelloService;
    }

    public void run() {
      List<String> strings1 = new ArrayList<>();
      strings1.add(Thread.currentThread().getId() + "cjsff");
      List<String> strings = sayHelloService.sayHello(strings1);
      for (String string : strings) {
        System.out.println(string);
      }
    }

  }
}
