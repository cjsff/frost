package com.cjsff.registry.zookeeper;

import com.cjsff.registry.ServiceRegisterDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rick
 */
@Slf4j
public class ZookeeperHandler implements ServiceRegisterDiscovery {

  private CuratorFramework client;
  private static final String ZK_DELIMITER = "/";
  private static final String FRPC_ZK_ROOT_PATH = "/frpc/";
  private static final int DEFAULT_ZK_SESSION_TIMEOUT_MS = 5000;
  private static final int DEFAULT_ZK_BASE_SLEEP_TIME_MS = 1000;
  private static final int DEFAULT_ZK_MAX_RETRIES = 3;
  private final Map<String, List<String>> interfaceNameToNodesMap = new ConcurrentHashMap<>(27);


  @Override
  public void start(String address) {
    ExponentialBackoffRetry retryPolicy =
            new ExponentialBackoffRetry
                    (
                            DEFAULT_ZK_BASE_SLEEP_TIME_MS,
                            DEFAULT_ZK_MAX_RETRIES
                    );
    client =
            CuratorFrameworkFactory.builder()
                    .connectString(address)
                    .retryPolicy(retryPolicy)
                    .sessionTimeoutMs(DEFAULT_ZK_SESSION_TIMEOUT_MS)
                    .build();
    client.getConnectionStateListenable().addListener((CuratorFramework curatorFramework,
                                                       ConnectionState connectionState) -> {
      if (ConnectionState.CONNECTED.equals(connectionState)) {
        log.info("The registration center is connected successfully");
      }
    });
    client.start();
  }

  @Override
  public List<String> discovery(String serviceName) {

    List<String> nodes = interfaceNameToNodesMap.get(serviceName);

    if (CollectionUtils.isNotEmpty(nodes)) {
      return nodes;
    }

    String serviceNamePath = providerPath(serviceName);

    try {
      nodes = client.getChildren().forPath(serviceNamePath);
      interfaceNameToNodesMap.put(serviceName,nodes);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return nodes;
  }


  @Override
  public void registered(String serviceName, String host, int port) {

    String serviceNamePath = providerPath(serviceName);

    if (!exists(serviceNamePath)) {
      create(serviceNamePath, false);
    }

    String nodePath = serviceNamePath + ZK_DELIMITER + host + ":" + port;

    create(nodePath, true);
  }

  private String providerPath(String service) {
    return FRPC_ZK_ROOT_PATH + service + ZK_DELIMITER + "provider";
  }

  public boolean exists(String path) {
    try {
      if (null != client.checkExists().forPath(path)) {
        return true;
      }
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
    return false;
  }

  public void create(String path, boolean ephemeral) {
    if (ephemeral) {
      this.createEphemeral(path);
    } else {
      this.createPersistent(path);
    }
  }

  public void createPersistent(String path) {
    try {
      client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  public void createEphemeral(String path) {
    try {
      client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
