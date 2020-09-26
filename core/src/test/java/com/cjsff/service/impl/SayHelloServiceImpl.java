package com.cjsff.service.impl;

import com.cjsff.service.SayHelloService;

/**
 * @author cjsff
 */
public class SayHelloServiceImpl implements SayHelloService {
    @Override
    public String sayHello(String name) {
        return "hello," + name;
    }
}
