package com.cjsff.service.impl;

import com.cjsff.service.SayHelloService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rick
 */
public class SayHelloServiceImpl implements SayHelloService {

  @Override
  public String sayHello(String name) {
    return "hello," + name;
  }
}
