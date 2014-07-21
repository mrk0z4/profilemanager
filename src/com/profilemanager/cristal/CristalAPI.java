package com.profilemanager.cristal;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mc on 7/21/14.
 */
public class CristalAPI {

    private final static String SERVICE_NAME = "_cristal._tcp.local.";

    private JmDNS jmdns;
    private boolean isConnected;

    public CristalAPI(){
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(addr.getHostName()).toString();
            jmdns = JmDNS.create(addr, hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToService(){
        jmdns.addServiceListener(SERVICE_NAME, new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent serviceEvent) {
                System.out.println("Cristal Service found.");
                isConnected = true;
            }

            @Override
            public void serviceRemoved(ServiceEvent serviceEvent) {
                isConnected = false;
            }

            @Override
            public void serviceResolved(ServiceEvent serviceEvent) {

            }
        });
    }

    public void disconnectService(){
        try {
            jmdns.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnectedToService(){
        return isConnected;
    }
}
