package com.profilemanager.cristal;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A class that contains the interface to connect with the cristal service.
 *
 * It searches the service via DNS Service Discovery in the LAN and bond to it. Provides the basic functionality for
 * Interact with the smart glass, connected to the service.
 *
 * TODO: make this class generic for using with another types of services.
 * Created by mc on 7/21/14.
 */
public class CristalAPI {

    private final static String SERVICE_NAME = "_cristal._tcp.local.";

    private JmDNS jmdns;
    private boolean isConnected;

    /**
     * Constructor.
     *
     * It does not connect to the service automatically.
     */
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

    /**
     * Search and implements a listener for the discovery of service in LAN.
     */
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

    /**
     * Disconnect from the service and stop it.
     */
    public void disconnectService(){
        try {
            jmdns.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether we are connected to the service.
     * @return true if is connected, false otherwise.
     */
    public boolean isConnectedToService(){
        return isConnected;
    }
}
