package com.cjsff.registry;


/**
 * @author cjsff
 */
public interface ServerRegisterDiscovery {

    String discovery();

    void register(int port);

}
