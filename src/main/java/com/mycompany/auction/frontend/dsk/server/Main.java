package com.mycompany.auction.frontend.dsk.server;

import com.mycompany.auction.frontend.dsk.server.frames.MainFrame;
import com.mycompany.auction.frontend.dsk.server.panel.HomePanel;
import com.mycompany.auction.frontend.dsk.server.service.ClientService;
import com.mycompany.auction.frontend.dsk.server.service.MulticastService;
import java.awt.BorderLayout;
import java.io.IOException;

public class Main {

    public static MainFrame window = new MainFrame();
    public static HomePanel homePanel = new HomePanel();
    public static ClientService clientService;
    public static MulticastService multicastService;

    public static void main(String[] args) throws IOException {
        showScreen();
    }

    public static void showScreen() {
        window.getContentPane().removeAll();
        window.setLayout(new BorderLayout());
        window.add(homePanel, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
    }
}
