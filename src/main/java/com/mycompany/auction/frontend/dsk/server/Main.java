package com.mycompany.auction.frontend.dsk.server;

import com.mycompany.auction.frontend.dsk.server.frames.MainFrame;
import com.mycompany.auction.frontend.dsk.server.panel.GamePanel;
import com.mycompany.auction.frontend.dsk.server.panel.HomePanel;
import com.mycompany.auction.frontend.dsk.server.panel.LoadingPanel;
import com.mycompany.auction.frontend.dsk.server.service.ClientService;
import com.mycompany.auction.frontend.dsk.server.service.LoginService;
import com.mycompany.auction.frontend.dsk.server.service.MulticastService;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JPanel;

public class Main {

    public static MainFrame window = new MainFrame();
    public static HomePanel homePanel = new HomePanel();
    public static LoadingPanel loadingPanel = new LoadingPanel();
    public static GamePanel gamePanel = new GamePanel();
    public static ClientService clientService;
    public static MulticastService multicastService = new MulticastService();
    public static LoginService loginService;

    public static void main(String[] args) {
        showScreen(homePanel);
    }

    public static void showScreen(JPanel newJPanel) {
        window.getContentPane().removeAll();
        window.setLayout(new BorderLayout());
        window.add(newJPanel, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
    }
}
