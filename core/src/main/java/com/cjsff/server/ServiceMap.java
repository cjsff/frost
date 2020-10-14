package com.cjsff.server;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rick
 */
public class ServiceMap {


  private ServiceMap(){}

  public static ServiceMap serviceMap = new ServiceMap();

  private final Map<String, Object> objectMap = new HashMap<>();

  public static ServiceMap getInstance() {
    return serviceMap;
  }

  public void put(String serviceName, Object o) {
    objectMap.put(serviceName, o);
  }

  public Map<String,Object> getObjectMap() {
    return objectMap;
  }
}
