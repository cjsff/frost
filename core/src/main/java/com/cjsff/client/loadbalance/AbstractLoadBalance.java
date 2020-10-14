package com.cjsff.client.loadbalance;

import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡策略抽象类
 *
 * @author rick
 */
public abstract class AbstractLoadBalance implements LoadBalanceStrategy {

  @Override
  public Channel select(Map<String, List<Channel>> serviceNameToClientListMap, String serviceName) {
    if (MapUtils.isEmpty(serviceNameToClientListMap)) {
      return null;
    }
    List<Channel> channelList = serviceNameToClientListMap.get(serviceName);
    if (CollectionUtils.isEmpty(channelList)) {
      return null;
    }
    return doSelect(channelList);
  }

  protected abstract Channel doSelect(List<Channel> channelList);
}
