package com.cjsff.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author rick
 */
public class SpiUtil {

  /**
   * Load the service according to the specified interface
   * @param clazz interface class
   * @return interface class implement
   */
  public static Object loadService(Class<?> clazz) {

    ServiceLoader<Object> serviceLoader = ServiceLoader.load((Class<Object>) clazz);
    List<Object> loadObjectList = new ArrayList<>();

    for (Object o : serviceLoader) {
      loadObjectList.add(o);
    }


    if (loadObjectList.size() > 1) {
      throw new RuntimeException("only one service can be loaded");
    }

    return loadObjectList.get(0);
  }

}
