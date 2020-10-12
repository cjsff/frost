package com.cjsff.serialization.impl;

import com.alibaba.fastjson.JSON;
import com.cjsff.serialization.Serialization;

/**
 * @author rick
 */
public class FastJsonSerializer implements Serialization {

    private static final byte FAST_JSON_SERIALIZER_TYPE = 1;

    @Override
    public byte[] serialize(Object o) {
        return JSON.toJSONBytes(o);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    @Override
    public byte getSerializationAlgorithm() {
        return FAST_JSON_SERIALIZER_TYPE;
    }
}
