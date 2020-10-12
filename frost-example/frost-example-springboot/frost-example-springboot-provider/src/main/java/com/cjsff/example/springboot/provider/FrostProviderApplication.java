package com.cjsff.example.springboot.provider;

import com.cjsff.springboot.EnableFrpcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rick
 */
@SpringBootApplication
@EnableFrpcConfiguration
public class FrostProviderApplication {

  public static void main(String[] args) {
    SpringApplication.run(FrostProviderApplication.class, args);

    synchronized (FrostProviderApplication.class) {
      try {
        FrostProviderApplication.class.wait();
      } catch (Throwable e) {
      }
    }
  }

}
