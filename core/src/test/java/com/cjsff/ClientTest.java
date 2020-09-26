package com.cjsff;

import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcClientOption;
import com.cjsff.client.FrpcProxy;
import com.cjsff.service.SayHelloService;
import com.cjsff.service.impl.SayHelloServiceImpl;

import java.net.InetSocketAddress;

public class ClientTest {
  public static void main(String[] args) throws InterruptedException {
    FrpcClientOption option = new FrpcClientOption();
    InetSocketAddress serverAddress = new InetSocketAddress(10027);
    FrpcClient client = new FrpcClient(serverAddress);
    long startTime = System.currentTimeMillis();
    int requestNum = 10;
    SayHelloService sayHelloService = FrpcProxy.getProxy(SayHelloServiceImpl.class, client);
    String cjsff = sayHelloService.sayHello("cjsff");
    System.out.println(cjsff);

    Thread[] threads = new Thread[100];

    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(new ThreadTask(sayHelloService));
      threads[i].start();
    }


    for (Thread thread : threads) {
      thread.join();
    }

  }


  public static class ThreadTask implements Runnable {

    private SayHelloService sayHelloService;

    public ThreadTask(SayHelloService sayHelloService) {
      this.sayHelloService = sayHelloService;
    }

    public void run() {

      sayHelloService.sayHello("john");
    }

  }
}
