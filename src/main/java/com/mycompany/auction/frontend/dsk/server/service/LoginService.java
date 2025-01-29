package com.mycompany.auction.frontend.dsk.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.auction.frontend.dsk.server.Main;
import com.mycompany.auction.frontend.dsk.server.entities.Client;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class LoginService {

    private final String certificatesDir = System.getProperty("user.dir") + "\\clients";
    private Client clientLogged;

    public boolean isClientRegistered(File clientCertificateName) {
        return clientCertificateName.exists() && clientCertificateName.isFile();
    }

    public boolean loginUser(String CPF) throws IOException {
        File jsonFile = new File(this.certificatesDir + "\\" + CPF + ".json");
        if (isClientRegistered(jsonFile)) {

            Main.clientService = new ClientService(); //conecta ao servidor
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeFile = objectMapper.readTree(jsonFile);
            ObjectNode jsonToSend = objectMapper.createObjectNode();
            
            this.clientLogged = new Client(jsonNodeFile.get("username").asText(), jsonNodeFile.get("name").asText(), jsonNodeFile.get("cpf").asText());
            
            jsonToSend.put("operation", "LOGIN");
            jsonToSend.put("username", this.clientLogged.getUsername());
            jsonToSend.put("cpf", this.clientLogged.getCpf());
            
            Main.clientService.sendLoginMessageToServer(jsonToSend.toString());

        }
        return true;
    }

    public Client getClientLogged() {
        return clientLogged;
    }
    

}
