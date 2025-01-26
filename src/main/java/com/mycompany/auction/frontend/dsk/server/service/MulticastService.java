package com.mycompany.auction.frontend.dsk.server.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastService {

    private MulticastSocket socket;
    private InetAddress group = null;
    
    public void joinGroup(String multicastGroup, int port) throws IOException {
        this.socket = new MulticastSocket(port);
        group = InetAddress.getByName(multicastGroup);
        socket.joinGroup(group);

        System.out.println("Joined Multicast group!");
    }

    
   
    
}
