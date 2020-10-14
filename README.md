# 核心功能
* 支持spring-boot-starter 启动
* RPC功能可独立使用,不用必须依赖注册中心功能
* 基于SPI机制可扩展序列化方式、服务注册发现、负载均衡策略

## 快速开始
### 开发环境
Java8 && maven3

[FrpcTest.java](https://github.com/cjsff/frost/blob/master/core/src/test/java/com/cjsff/FrpcTest.java)
#### API 启动方式
##### Server端
1.通过指定端口完成初始化
2.服务注册,如果要使用服务注册发现功能添加zookeeper地址即可

##### Client端
1.初始化
2.通过Server端地址/zookeeper地址和调用类名称获取Channel连接
3.获取调用类代理对象
```java
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
```
[FrostProviderApplication.java](https://github.com/cjsff/frost/blob/master/frost-example/frost-example-springboot/frost-example-springboot-provider/src/main/java/com/cjsff/example/springboot/provider/FrostProviderApplication.java)
<br/>
[HelloServiceImpl.java](https://github.com/cjsff/frost/blob/master/frost-example/frost-example-springboot/frost-example-springboot-provider/src/main/java/com/cjsff/example/springboot/provider/impl/HelloServiceImpl.java)
<br/>
[application.properties](https://github.com/cjsff/frost/blob/master/frost-example/frost-example-springboot/frost-example-springboot-provider/src/main/resources/application.properties)
#### spring-boot 启动方式
##### Server端
1.在spring-boot启动类添加@EnableFrpcConfiguration注解.
<br/>
2.application.properties配置中添加spring.frpc.port(Server启动端口Integer类型),
spring.frpc.server(是否开启Server服务Boolean类型),如果要使用注册中心功能添加
spring.frpc.zookeeperAddress填写zookeeper地址即可.
<br/>
3.接口实现类添加@Component,@FrpcServiceProvider(interfaceClass参数需要填写实现接口类)注解.


[FrostConsumerApplication.java](https://github.com/cjsff/frost/blob/master/frost-example/frost-example-springboot/frost-example-springboot-consumer/src/main/java/com/cjsff/example/springboot/consumer/FrostConsumerApplication.java)
<br/>
[HelloController.java](https://github.com/cjsff/frost/blob/master/frost-example/frost-example-springboot/frost-example-springboot-consumer/src/main/java/com/cjsff/example/springboot/consumer/controller/HelloController.java)
##### Client端
1.在spring-boot启动类添加@EnableFrpcConfiguration注解.
<br/>
2.调用接口类添加@FrpcServiceConsumer注解,url参数需要填写获取Server连接方式和地址,
直连Server端填写frpc://Server端地址,从zookeeper中获取Server端地址填写zookeeper://zookeeper地址





### 实现过程中碰到不会的功能都会去参考以下项目(排名分先后)

# 感谢
[brpc-java](https://github.com/baidu/brpc-java)
<br/>
[guide-rpc-framework](https://github.com/Snailclimb/guide-rpc-framework)
<br/>
[dubbo](https://github.com/apache/dubbo)
<br/>
[dubbo-spring-boot-starter](https://github.com/alibaba/dubbo-spring-boot-starter)
