package com.cjsff;


import com.cjsff.server.FrpcServer;

public class ServerTest {

  public static void main(String[] args) throws InterruptedException {
    FrpcServer server = new FrpcServer(10027);
  }

}
