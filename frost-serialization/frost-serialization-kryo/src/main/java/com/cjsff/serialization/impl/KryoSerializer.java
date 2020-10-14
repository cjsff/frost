package com.cjsff.serialization.impl;

import com.cjsff.serialization.Serialization;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author rick
 */
public class KryoSerializer implements Serialization {

  private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(Kryo::new);


  @Override
  public byte[] serialize(Object o) {

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         Output output = new Output(byteArrayOutputStream)) {
      Kryo kryo = kryoThreadLocal.get();
      kryo.register(o.getClass());

      kryo.writeObject(output, o);

      kryoThreadLocal.remove();

      return output.toBytes();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("serialize exception, className=" + o.getClass().getName());
    }
  }

  @Override
  public <T> T deserialize(byte[] bytes, Class<T> clazz) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
         Input input = new Input(byteArrayInputStream)) {
      Kryo kryo = kryoThreadLocal.get();
      kryo.register(clazz);

      Object o = kryo.readObject(input, clazz);

      kryoThreadLocal.remove();

      return clazz.cast(o);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("deserialize exception, className=" + clazz.getName());
    }
  }

}
