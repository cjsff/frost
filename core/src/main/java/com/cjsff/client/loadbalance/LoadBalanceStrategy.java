package com.cjsff.client.loadbalance;

import com.cjsff.common.annotation.SPI;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

/**
 * @author rick
 */
@SPI
public interface LoadBalanceStrategy {


  /**
   * select request client
   *
   * @param serviceNameToClientListMap all client channel
   * @param serviceName                request service name
   * @return request client
   */
  Channel select(Map<String, List<Channel>> serviceNameToClientListMap, String serviceName);

}
