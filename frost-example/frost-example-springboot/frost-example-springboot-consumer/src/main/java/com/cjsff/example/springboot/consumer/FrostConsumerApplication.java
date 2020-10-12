package com.cjsff.example.springboot.consumer;

import com.cjsff.springboot.EnableFrpcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rick
 */
@SpringBootApplication
@EnableFrpcConfiguration
public class FrostConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FrostConsumerApplication.class, args);
  }

}
