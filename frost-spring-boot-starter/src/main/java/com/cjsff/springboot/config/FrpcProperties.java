package com.cjsff.springboot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rick
 */
@ConfigurationProperties(prefix = "spring.frpc")
@Setter
@Getter
public class FrpcProperties {
  private int port;
  private boolean server;
  private String zookeeperAddress;
}
