package com.mycompany.auction.frontend.dsk.server.cryptography;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.auction.frontend.dsk.server.Main;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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

    public boolean verifyMessage(String decryptedMessage, byte[] hashEncrypted) {

        byte[] messageHash = md.digest(decryptedMessage.getBytes(StandardCharsets.UTF_8));

        if (verifySignedHash(messageHash, hashEncrypted)){
            defineSecretKey(decryptedMessage);
            return true;
        }
        
        return false;
    }

    public String decryptAssymmetric(String encryptedMessage) {
        try {
            PrivateKey clientPrK = getClientPrivateKey(Main.loginService.getClientLogged().getCpf());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, clientPrK);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

            return new String(decryptedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException
                | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String encryptSymmetric(String message) {
        try {
            byte[] iv = "1234567890123456".getBytes(StandardCharsets.UTF_8); // Exemplo de IV fixo
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKey serverSymmKey = this.serverSymmetricKey;

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverSymmKey, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public String decryptSymmetric(String encryptedMessage) {
        try {
            byte[] iv = "1234567890123456".getBytes(StandardCharsets.UTF_8); 
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKey serverSymmKey = this.serverSymmetricKey; 

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, serverSymmKey, ivParameterSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public PrivateKey getClientPrivateKey(String CPF) {
        File jsonFile = new File(this.certificatesDir + "\\clients\\" + CPF + ".json");

        if (isClientRegistered(jsonFile)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonFile); 

                JsonNode privateKeyNode = rootNode.get("private-key");
                if (privateKeyNode != null) {
                    String privateKeyBase64 = privateKeyNode.asText(); 

                    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    return keyFactory.generatePrivate(keySpec); 
                } else {
                    System.out.println("Campo 'private-key' não encontrado no arquivo.");
                }
            } catch (Exception e) {
                System.out.println("Erro ao ler ou converter a chave privada: " + e.getMessage());
            }
        }

        return null; // Caso não encontre a chave ou algum erro ocorra
    }

    public PublicKey getServerPublicKey() {
        File jsonFile = new File(this.certificatesDir + "\\server.json");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile); // Lê o arquivo JSON

            JsonNode publicKeyNode = rootNode.get("public-key");
            if (publicKeyNode != null) {
                String publicKeyBase64 = publicKeyNode.asText();

                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                return keyFactory.generatePublic(keySpec); // Retorna a chave pública
            } else {
                System.out.println("Campo 'public-key' não encontrado no arquivo.");
            }
        } catch (IOException | java.security.NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException e) {
            System.out.println("Erro ao ler ou converter a chave pública: " + e.getMessage());
        }

        return null;
    }

    public boolean verifySignedHash(byte[] hashBytes, byte[] signedHashBytes) {
        try {
            PublicKey serverPublicKey = getServerPublicKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(serverPublicKey);
            signature.update(hashBytes);
            return signature.verify(signedHashBytes); 
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public byte[] calculateHash(String message) {
        byte[] hashBytes = md.digest(message.getBytes(StandardCharsets.UTF_8));
        return hashBytes;
    }

    private void defineSecretKey(String decryptedMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decryptedMessage);

            if (jsonNode.has("symmetric_key")) {
                byte[] keyBytes = Base64.getDecoder().decode(jsonNode.get("symmetric_key").asText());
                this.serverSymmetricKey = new SecretKeySpec(keyBytes, "AES");
            }

        } catch (JsonProcessingException e) {
            System.out.println("Deu erro definindo a simmetric key");
            System.out.println(e.getMessage());
        }
    }
}
