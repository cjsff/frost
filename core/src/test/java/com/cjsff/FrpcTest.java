package com.cjsff;


import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcProxy;
import com.cjsff.server.FrpcServer;
import com.cjsff.service.SayHelloService;
import com.cjsff.service.impl.SayHelloServiceImpl;
import org.junit.Assert;
import org.junit.Test;


public class FrpcTest {


  @Test
  public void sayHelloTest() throws InterruptedException {
    FrpcServer frpcServer = new FrpcServer(10027);

    // use zookeeper
    // frpcServer.addService(SayHelloService.class.getName(), new SayHelloServiceImpl(),
    // "localhost:2181");

    frpcServer.addService(SayHelloService.class.getName(),new SayHelloServiceImpl(),null);


    FrpcClient client = new FrpcClient();

    // use zookeeper
    // client.initChannelFromRegistry("localhost:2181", SayHelloService.class.getName());

    client.initChannelFromServerNodeAddress("localhost:10027",SayHelloService.class.getName());

    SayHelloService sayHelloService = FrpcProxy.getProxy(SayHelloService.class, client);

    String sayHello = sayHelloService.sayHello("cjsff");

    Assert.assertEquals("hello,cjsff",sayHello);
  }

}
