package com.cjsff.utils;

import com.cjsff.registry.RegisterInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author rick
 */
public class NetUtils {

    public static RegisterInfo getRegisterInfo(String address) {
        int index = address.indexOf(":");
        return new RegisterInfo(address.substring(0, index), Integer.parseInt(address.substring(index + 1)));
    }

    @SuppressWarnings("rawtypes")
    public static String getHostAddress() {
        // Local IP, if internet IP is not configured, return it
        String local = null;
        // internet IP
        String net = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            boolean isNet = false;
            while (netInterfaces.hasMoreElements() && !isNet) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = (InetAddress) address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        net = ip.getHostAddress();
                        isNet = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(
                            ":")) {
                        local = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (net != null && !"".equals(net)) {
            return net;
        } else {
            return local;
        }
    }

}
