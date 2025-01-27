package com.mycompany.auction.frontend.dsk.server.service;

import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastService implements Runnable{

    private MulticastSocket socket;
    private InetAddress group = null;

    public void joinGroup(String multicastGroup, int port) throws IOException {
        this.socket = new MulticastSocket(port);
        group = InetAddress.getByName(multicastGroup);
        socket.joinGroup(group);

        System.out.println("Joined Multicast group!");
        Main.showScreen(Main.loadingPanel);
    }

    public void listenForMessages() {
        try {
            byte[] buffer = new byte[256]; // Tamanho do buffer para armazenar os pacotes recebidos
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Listening for messages in the multicast group...");

            while (true) {
                // Espera por pacotes no grupo multicast
                socket.receive(packet);

                // Extrai a mensagem recebida do pacote
                String message = new String(packet.getData(), 0, packet.getLength());

                // Exibe a mensagem recebida
                System.out.println("Mensagem recebida: " + message);

                // Você pode adicionar um processamento adicional da mensagem aqui, se necessário.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.listenForMessages();
    }
}
