package com.cjsff.transport;


import com.cjsff.serialization.Serialization;

/**
 * @author rick
 */
public class SerializationManager {

  private Serialization serialization;

  private SerializationManager(){}

  private static SerializationManager instance;

  public static SerializationManager getInstance() {
    if (null == instance) {
      synchronized (SerializationManager.class) {
        if (null == instance) {
          instance = new SerializationManager();
        }
      }
    }

    return instance;
  }

  public Serialization getSerialization() {
    return serialization;
  }

  public void setSerialization(Serialization serialization) {
    this.serialization = serialization;
  }
}
