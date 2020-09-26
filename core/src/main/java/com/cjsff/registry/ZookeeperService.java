package com.cjsff.registry;

import com.cjsff.utils.NetUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cjsff
 */
public class ZookeeperService implements ServerRegisterDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperService.class);

    private CuratorFramework zkClient;

    public ZookeeperService(String registerAddress) {
        ExponentialBackoffRetry retryPolicy =
                new ExponentialBackoffRetry
                        (
                                Constants.DEFAULT_ZK_BASE_SLEEP_TIME_MS,
                                Constants.DEFAULT_ZK_MAX_RETRIES
                        );
        zkClient =
                CuratorFrameworkFactory.builder()
                        .connectString(registerAddress)
                        .retryPolicy(retryPolicy)
                        .sessionTimeoutMs(Constants.DEFAULT_ZK_SESSION_TIMEOUT_MS)
                        .build();
        zkClient.start();
    }

    @Override
    public String discovery() {
        try {
            byte[] serverAddressByte = zkClient.getData().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH);
            return new String(serverAddressByte);
        } catch (Exception e) {
            log.error("No available services.");
        }
        return null;
    }

    @Override
    public void register(int port) {
        try {
            if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_ROOT_PATH) == null) {
                zkClient.create().forPath(Constants.DEFAULT_ZK_ROOT_PATH);
                createNode(zkClient, NetUtils.getHostAddress() + ":" + port);
            }
        } catch (Exception e) {
            log.error("add root node error : {}", e.getMessage());
        }

    }


    private void createNode(CuratorFramework zkClient, String serverAddress) {
        try {
            if (zkClient.checkExists().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH) == null) {
                zkClient.create().forPath(Constants.DEFAULT_ZK_REGISTRY_DATA_PATH, serverAddress.getBytes());
            }
        } catch (Exception e) {
            log.error("create node error : {}", e.getMessage());
        }
    }
}
