package com.mycompany.auction.frontend.dsk.server.panel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class GamePanel extends javax.swing.JPanel {

    
    private Timer swingTimer;

    public GamePanel() {
        initComponents();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        productNameField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        startPriceField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        minimumBidField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        winningPlayerField = new javax.swing.JTextField();
        raiseBidBtn = new javax.swing.JButton();
        yourNameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        timerField = new javax.swing.JTextField();
        timerLabel = new javax.swing.JLabel();

        productNameField.setEditable(false);

        jLabel1.setText("Product");

        startPriceField.setEditable(false);

        jLabel2.setText("Price");

        jLabel3.setText("Minimum Bid");

        minimumBidField.setEditable(false);

        jLabel4.setText("Winning Player");

        winningPlayerField.setEditable(false);

        raiseBidBtn.setText("Raise the Bid ");
        raiseBidBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                raiseBidBtnMouseClicked(evt);
            }
        });

        yourNameField.setEditable(false);

        jLabel6.setText("You:");

        timerField.setEditable(false);

        timerLabel.setText("Time to End:");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(minimumBidField, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(winningPlayerField, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(raiseBidBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(yourNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timerField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timerLabel))
                .addGap(73, 73, 73))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(productNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(timerLabel))
                .addGap(5, 5, 5)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timerField, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(minimumBidField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(jLabel4)
                .addGap(5, 5, 5)
                .addComponent(winningPlayerField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(raiseBidBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yourNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void raiseBidBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_raiseBidBtnMouseClicked
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            int minimumBid = Integer.valueOf(minimumBidField.getText());

            jsonNode.put("operation", "RAISE BID");
            jsonNode.put("username", Main.loginService.getClientLogged().getUsername());
            jsonNode.put("bid", minimumBid);

            Main.multicastService.sendMessageToGroup(jsonNode.toString());
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_raiseBidBtnMouseClicked

    public void updateAuctionRoundScreenInfo(String message) {
        try {
            yourNameField.setText(Main.loginService.getClientLogged().getUsername());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            String startPriceTxt = String.valueOf(jsonNode.get("start_price").asInt());
            
            productNameField.setText(jsonNode.get("product").asText());
            startPriceField.setText(startPriceTxt);
            minimumBidField.setText(jsonNode.get("minimumBid").asText());
            winningPlayerField.setText(jsonNode.get("current-winner").asText());

        } catch (JsonProcessingException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateWinnerAndCurrentPrice(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        String currentPriceTxt = String.valueOf(jsonNode.get("current-price").asInt());

        startPriceField.setText(currentPriceTxt);
        winningPlayerField.setText(jsonNode.get("current-winner").asText());
    }
    
    public void startCountdown(LocalDateTime timeToEnd) {
        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                Duration duration = Duration.between(LocalDateTime.now(), timeToEnd);
                long secondsLeft = duration.getSeconds();

                if (secondsLeft > 0) {
                    long hours = secondsLeft / 3600;
                    long minutes = (secondsLeft % 3600) / 60;
                    long seconds = secondsLeft % 60;

                    timerField.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    swingTimer.stop();
                    timerField.setText("00:00:00");
                }
            }
        });

        swingTimer.start(); // Inicia o Timer
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField minimumBidField;
    private javax.swing.JTextField productNameField;
    private javax.swing.JButton raiseBidBtn;
    private javax.swing.JTextField startPriceField;
    private javax.swing.JTextField timerField;
    private javax.swing.JLabel timerLabel;
    private javax.swing.JTextField winningPlayerField;
    private javax.swing.JTextField yourNameField;
    // End of variables declaration//GEN-END:variables
}
