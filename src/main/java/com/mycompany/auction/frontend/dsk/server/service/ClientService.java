package com.mycompany.auction.frontend.dsk.server.service;

import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.IOException;

public class ClientService {
    
    private SocketService socketService;

    public ClientService() throws IOException {
        this.socketService = new SocketService();
    }
    
    public void sendLoginMessageToServer(String message) throws IOException{
        socketService.sendMessageToServer(message);
    }
    
    
}
