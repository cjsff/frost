package com.cjsff.client.loadbalance;


import io.netty.channel.Channel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * Random load strategy
 *
 * @author rick
 */
public class RandomLoadBalance extends AbstractLoadBalance {


  @Override
  protected Channel doSelect(List<Channel> channelList) {
    if (CollectionUtils.isEmpty(channelList)) {
      return null;
    }

    int length = channelList.size();
    Random random = new Random();
    return channelList.get(random.nextInt(length));
  }
}