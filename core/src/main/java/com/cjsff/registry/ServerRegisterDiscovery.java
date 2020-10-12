package com.cjsff.registry;


/**
 * @author rick
 */
public interface ServerRegisterDiscovery {

    String discovery();

    void register(int port);

}
