package com.mycompany.auction.frontend.dsk.server.cryptography;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptService {

    private final String certificatesDir = System.getProperty("user.dir");
    private MessageDigest md;
    private SecretKey serverSymmetricKey;

    public EncryptService() {
        try {
            this.md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isClientRegistered(File clientCertificateName) {
        return clientCertificateName.exists() && clientCertificateName.isFile();
    }

    public String decryptAssymmetric(String encryptedMessage) {
        //precisa da chave privada do cliente logado no momento. -> check
        //aplicar a chave privada para decriptografar a mensagem - check
        //verificar se existe a key "hash" - check
        //comparar o hash
        try {
            PrivateKey clientPrK = getClientPrivateKey(Main.loginService.getClientLogged().getCpf());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, clientPrK);

            System.out.println("Decriptando...");
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

            String decryptedMessage = new String(decryptedBytes);
            System.out.println("Mensagem decriptografada: " + decryptedMessage);

//            if (verifySignature(decryptedMessage)) {
//                System.out.println("Hash identico!");
//            }
            defineSecretKey(decryptedMessage);

            return new String(decryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException
                | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String encryptSymmetric(String message) {
        System.out.println("Mensagem antes de encriptar: " + message);
        try {
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, this.serverSymmetricKey);

            byte[] messageBytes = message.getBytes();

            byte[] encryptedBytes = cipher.doFinal(messageBytes);
            
            System.out.println("Mensagem encriptada: " + Base64.getEncoder().encodeToString(encryptedBytes));
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.out.println("Deu erro encriptando");
            System.out.println(e.getMessage());
        }
        
        return null;
    }
    
    public String decryptSymmetric(String encryptMessage) {
        System.out.println("Mensagem ENCRIPTADA: " + encryptMessage );
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, this.serverSymmetricKey);

            // Decodificar a mensagem criptografada que está em base64
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptMessage);
            
            // Descriptografar os dados
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            System.out.println("Mensagem decriptografada: " + new String(decryptedBytes));

            return new String(decryptedBytes);
        } catch (Exception e) {
            System.out.println("Deu erro ao tentar decriptografar");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    

    public PrivateKey getClientPrivateKey(String CPF) {
        // Caminho do arquivo JSON
        File jsonFile = new File(this.certificatesDir + "\\clients\\" + CPF + ".json");

        // Verifica se o cliente está registrado
        if (isClientRegistered(jsonFile)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonFile); // Lê o arquivo JSON

                // Recupera o nó da chave privada
                JsonNode privateKeyNode = rootNode.get("private-key");
                if (privateKeyNode != null) {
                    String privateKeyBase64 = privateKeyNode.asText(); // Obtém a chave em Base64

                    // Decodifica a chave privada de Base64 para bytes
                    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

                    // Cria a especificação de chave usando os bytes decodificados
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

                    // Utiliza o KeyFactory para gerar a chave privada a partir da especificação
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    return keyFactory.generatePrivate(keySpec); // Retorna a chave privada
                } else {
                    System.out.println("Campo 'private-key' não encontrado no arquivo.");
                }
            } catch (Exception e) {
                System.out.println("Erro ao ler ou converter a chave privada: " + e.getMessage());
            }
        }

        return null; // Caso não encontre a chave ou algum erro ocorra
    }

    public boolean verifySignature(String decryptedMessage) {
        try {
            System.out.println("Verificando se tem o hash");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decryptedMessage);

            if (jsonNode.has("hash")) {
                System.out.println("Tem o hash. Comparando...");
                String hashSended = jsonNode.get("hash").asText();
                System.out.println("Hash enviado: " + hashSended);

                String messageReceivedHash = calculateHash(decryptedMessage);
                System.out.println("Hash calculado: " + messageReceivedHash);

                return messageReceivedHash.equals(hashSended);

            }
        } catch (JsonProcessingException ex) {
            System.out.println("Foi descriptografado incorretamente.");
        }

        return false;
    }

    public String calculateHash(String message) {
        byte[] hashBytes = md.digest(message.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private void defineSecretKey(String decryptedMessage) {
        System.out.println("Definindo a simmetric key!");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decryptedMessage);

            if (jsonNode.has("symmetric-key")) {
                byte[] keyBytes = Base64.getDecoder().decode(jsonNode.get("symmetric-key").asText());
                System.out.println("Symmetric key: " +  Base64.getEncoder().encodeToString(keyBytes));
                this.serverSymmetricKey = new SecretKeySpec(keyBytes, "AES");
            }

        } catch (JsonProcessingException e) {
            System.out.println("Deu erro definindo a simmetric key");
            System.out.println(e.getMessage());
        }
    }
}
