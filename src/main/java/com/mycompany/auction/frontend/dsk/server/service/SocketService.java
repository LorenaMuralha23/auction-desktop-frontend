package com.mycompany.auction.frontend.dsk.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketService {

    private Socket socket;

    public SocketService() throws IOException {
        this.socket = new Socket("localhost", 5000);
        System.out.println("Conectado ao servidor!");
    }

    public void sendMessageToServer(String message) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        // Envia uma mensagem ao servidor
        System.out.println("Enviando messagem...");

        out.println(message);
        System.out.println("Mensagem enviada!");
        receiveData();
//        mapResponse(receiveMessageFromServer());
    }

    // Método para receber mensagem do servidor
    private void receiveData() {
        try (
                 DataInputStream in = new DataInputStream(this.socket.getInputStream());) {
            int encryptedLength = in.readInt();
            byte[] encryptedBytes = new byte[encryptedLength];
            in.readFully(encryptedBytes);

            // Recebe o tamanho do hash
            int hashLength = in.readInt();
            byte[] hashBytes = new byte[hashLength];
            in.readFully(hashBytes);

            // Converte os bytes para strings
            String encryptedResponse = new String(encryptedBytes, StandardCharsets.UTF_8);
            String decryptedResponse = Main.encryptService.decryptAssymmetric(encryptedResponse);

            if(Main.encryptService.verifyMessage(decryptedResponse, hashBytes)){
                mapResponse(decryptedResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
