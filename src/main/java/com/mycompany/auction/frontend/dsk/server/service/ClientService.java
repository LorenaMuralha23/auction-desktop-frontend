package com.mycompany.auction.frontend.dsk.server.service;

import com.mycompany.auction.frontend.dsk.server.MainServer;
import java.io.IOException;

public class ClientService {
    
    private MainServer socketService;

    public ClientService() throws IOException {
        this.socketService = new MainServer();
    }
    
    public void sendLoginMessageToServer(String message) throws IOException{
        socketService.sendMessageToServer(message);
    }
    
    
}
