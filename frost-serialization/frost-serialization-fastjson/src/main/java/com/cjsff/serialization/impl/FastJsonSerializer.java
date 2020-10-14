package com.cjsff.serialization.impl;

import com.alibaba.fastjson.JSON;
import com.cjsff.serialization.Serialization;

/**
 * @author rick
 */
public class FastJsonSerializer implements Serialization {

    @Override
    public byte[] serialize(Object o) {
        return JSON.toJSONBytes(o);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

}
