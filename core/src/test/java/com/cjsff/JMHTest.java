package com.cjsff;

import com.cjsff.client.FrpcClient;
import com.cjsff.client.FrpcProxy;
import com.cjsff.service.SayHelloService;
import com.cjsff.service.impl.SayHelloServiceImpl;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHTest {
  @Benchmark
  public int sleepAWhile() throws InterruptedException {
    sayHelloService.sayHello(new ArrayList<>());
    return 0;
  }

  private static SayHelloService sayHelloService;

  static {
    InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 10027);
    FrpcClient client = new FrpcClient(serverAddress);
    sayHelloService = FrpcProxy.getProxy(SayHelloServiceImpl.class, client);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
            .include(JMHTest.class.getSimpleName())
            .forks(1)
            .warmupIterations(5)
            .measurementIterations(5)
            .build();

    new Runner(opt).run();
  }
}
