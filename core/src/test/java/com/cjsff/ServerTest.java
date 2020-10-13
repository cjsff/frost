package com.cjsff;


import com.cjsff.server.FrpcServer;
import com.cjsff.server.FrpcServerOption;
import com.cjsff.service.SayHelloService;
import com.cjsff.service.impl.SayHelloServiceImpl;


public class ServerTest {

  public static void main(String[] args) throws InterruptedException {
    FrpcServerOption serverOption = new FrpcServerOption();
    serverOption.setNettyBossThreadNum(1);
    serverOption.setNettyWorkThreadNum(1);
    FrpcServer frpcServer = new FrpcServer(10027);

    frpcServer.addService(SayHelloService.class.getName(),new SayHelloServiceImpl(),
            "localhost:2181");

  }

}
