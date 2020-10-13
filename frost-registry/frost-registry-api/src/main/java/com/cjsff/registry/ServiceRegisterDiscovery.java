package com.cjsff.registry;

import java.util.List;

/**
 * @author rick
 */
public interface ServiceRegisterDiscovery {

  /**
   * Service discovery
   * @param serviceName
   * @return Service provider node  ip:port
   */
  List<String> discovery(String serviceName);


  /**
   * Service registered
   * @param serviceName
   * @param host
   * @param port
   */
  void registered(String serviceName,String host,int port);

  /**
   * Service registration client start
   * @param address Registered address
   */
  void start(String address);
}
