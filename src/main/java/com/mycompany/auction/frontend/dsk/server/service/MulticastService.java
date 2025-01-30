package com.mycompany.auction.frontend.dsk.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastService implements Runnable {

    private MulticastSocket socket;
    private InetAddress group = null;
    private int port;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean gameIsScheduled = false;

    public void joinGroup(String multicastGroup, int port, JsonNode jsonNode) throws IOException {
        this.socket = new MulticastSocket(port);
        this.port = port;
        group = InetAddress.getByName(multicastGroup);
        socket.joinGroup(group);

        System.out.println("Joined Multicast group!");

    }

    public void listenForMessages() {
        try {
            byte[] buffer = new byte[2048]; // Tamanho do buffer para armazenar os pacotes recebidos
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("\nListening for messages in the multicast group...");

            while (true) {
                Arrays.fill(buffer, (byte) 0); // Limpa o buffer antes de receber uma nova mensagem
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                String decryptedMsg = Main.encryptService.decryptSymmetric(message);
                System.out.println("Mensagem recebida: " + decryptedMsg);

                JsonNode jsonNode = objectMapper.readTree(decryptedMsg);

                if (!jsonNode.get("username").asText().equals(Main.loginService.getClientLogged().getUsername())) {
                    mapOperation(decryptedMsg);
                }
            }
        } catch (IOException e) {
            System.out.println("Deu erro F");
            System.out.println(e.getMessage());
        }
    }

    public void sendMessageToGroup(String message) throws IOException {
        String encryptedMsg = Main.encryptService.encryptSymmetric(message);
        DatagramPacket packet = new DatagramPacket(encryptedMsg.getBytes(), encryptedMsg.length(), this.group, this.port);

        socket.send(packet);
        System.out.println("\n[MESSAGE SEND TO MULTICAST GROUP]");
    }

    public void mapOperation(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);
        String operation = jsonNode.get("operation").asText();

        switch (operation) {
            case "SET INFO":
                System.out.println(message);
                LocalDateTime timeToStart = LocalDateTime.parse(jsonNode.get("timeToStart").asText());
                LocalDateTime timeToEnd = LocalDateTime.parse(jsonNode.get("timeToEnd").asText());
                long delayToStart = Duration.between(LocalDateTime.now(), timeToStart).toMillis();

                if (!gameIsScheduled) { //se for true, o usuario ja recebeu a info do game
                    Main.showScreen(Main.loadingPanel);

                    if (delayToStart > 0) {
                        scheduler.schedule(() -> {
                            this.gameIsScheduled = true;
                            Main.showScreen(Main.gamePanel);
                            Main.gamePanel.updateAuctionRoundScreenInfo(message);
                            Main.gamePanel.startCountdown(timeToEnd);
                        }, delayToStart, TimeUnit.MILLISECONDS);
                    } else {
                        Main.showScreen(Main.gamePanel);
                        Main.gamePanel.updateAuctionRoundScreenInfo(message);
                        Main.gamePanel.startCountdown(timeToEnd);
                    }
                }

                break;

            case "UPDATE WINNER":
                Main.gamePanel.updateWinnerAndCurrentPrice(jsonNode.toString());
                break;
            case "RAISE BID":
                break;
            case "FINISH ROUND":
                this.gameIsScheduled = false;
                String winnerUsernmame = jsonNode.get("winner").asText();
                Main.showScreen(Main.winnerPanel);
                Main.winnerPanel.updateWinner(winnerUsernmame);
                break;
        }
    }

    @Override
    public void run() {
        this.listenForMessages();
    }
}
