package com.cjsff.transport;

import com.alibaba.fastjson.JSON;

/**
 * @author cjsff
 */
public class JsonSerializer implements Serialization {

    @Override
    public byte[] serialize(Object o) {
        return JSON.toJSONBytes(o);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }
}
