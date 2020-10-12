package com.cjsff.spi;

import com.cjsff.serialization.Serialization;
import com.cjsff.transport.SerializationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author rick
 */
public class SerializationSpiManager {


  private SerializationSpiManager(){}


  public static SerializationSpiManager instance;

  public static SerializationSpiManager getInstance() {
    if (null == instance) {
      synchronized (SerializationSpiManager.class) {
        if (null == instance) {
          instance = new SerializationSpiManager();
        }
      }
    }
    return instance;
  }


  public void loadSerialization() {

    ServiceLoader<Serialization> serializationServiceLoader = ServiceLoader.load(Serialization.class);

    List<Serialization> serializationList = new ArrayList<>();
    for (Serialization serialization : serializationServiceLoader) {
      serializationList.add(serialization);
    }

    if (serializationList.size() > 1) {
      throw new RuntimeException("there can only be one serialization type");
    }

    Serialization serialization = serializationList.get(0);

    SerializationManager serializationManager = SerializationManager.getInstance();

    serializationManager.setSerialization(serialization);
  }

}
