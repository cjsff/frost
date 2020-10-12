package com.cjsff.serialization;

import com.cjsff.common.annotation.SPI;

/**
 * @author rick
 */
@SPI
public interface Serialization {



    /**
     * java obj to byte
     *
     * @param o
     * @return
     */
    byte[] serialize(Object o);

    /**
     * byte to java obj
     *
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

    /**
     * 获取序列化算法类型
     * @return
     */
    byte getSerializationAlgorithm();
}
