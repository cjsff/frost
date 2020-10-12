package com.cjsff;


import com.cjsff.server.FrpcServerOption;


public class ServerTest {

  public static void main(String[] args) throws InterruptedException {
    FrpcServerOption serverOption = new FrpcServerOption();
    serverOption.setNettyBossThreadNum(1);
    serverOption.setNettyWorkThreadNum(1);
  }

}
