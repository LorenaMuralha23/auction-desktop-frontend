package com.mycompany.auction.frontend.dsk.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketService {

    private Socket socket;

    public SocketService() throws IOException {
        this.socket = new Socket("localhost", 5000);
        System.out.println("Conectado ao servidor!");
    }

    public void sendMessageToServer(String message) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Envia uma mensagem ao servidor
        out.println(message);
        mapResponse(receiveMessageFromServer());
    }

    // Método para receber mensagem do servidor
    public String receiveMessageFromServer() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String serverMessage = in.readLine();
        
        System.out.println("Mensagem antes de descriptografar: " + serverMessage);
        String messageDecrypted = Main.encryptService.decrypt(serverMessage);
        
        return messageDecrypted;
    }

    public void mapResponse(String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            switch (jsonNode.get("login_status").asText()) {
                case "SUCCESS":
                  
                    Main.multicastService.joinGroup(jsonNode.get("group_address").asText(),
                            jsonNode.get("group_port").asInt(), jsonNode);
                    
                    break;
            }

        } catch (JsonProcessingException ex) {
            Logger.getLogger(SocketService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
