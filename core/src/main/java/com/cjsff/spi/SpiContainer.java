package com.cjsff.spi;

import com.cjsff.client.loadbalance.LoadBalanceStrategy;
import com.cjsff.registry.ServiceRegisterDiscovery;
import com.cjsff.serialization.Serialization;
import com.cjsff.utils.SpiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rick
 */
public class SpiContainer {

  private SpiContainer() {
  }

  public static final SpiContainer INSTANCE = new SpiContainer();

  private final Map<String, Object> interfaceNameToImplObjMap = new HashMap<>(10);

  private static final List<Class<?>> SPI_INTERFACE_LIST = new ArrayList<Class<?>>(){{
    add(ServiceRegisterDiscovery.class);
    add(Serialization.class);
    add(LoadBalanceStrategy.class);
  }};

  public static SpiContainer getInstance() {
    return INSTANCE;
  }


  public void load(boolean isServer) {

    if (isServer) {
      SPI_INTERFACE_LIST.remove(ServiceRegisterDiscovery.class);
    }

    for (Class<?> clazz : SPI_INTERFACE_LIST) {

      Object loadService = SpiUtil.loadService(clazz);

      interfaceNameToImplObjMap.put(clazz.getName(), loadService);

    }

  }


  public Object get(String interfaceName) {
    return interfaceNameToImplObjMap.get(interfaceName);
  }

}
