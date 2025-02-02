package com.mycompany.auction.frontend.dsk.server.cryptography;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
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

    public String decryptAssymmetric(String encryptedMessage) {
        try {

            PrivateKey clientPrK = getClientPrivateKey(Main.loginService.getClientLogged().getCpf());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, clientPrK);

            System.out.println("Decriptando...");
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

            String decryptedMessage = new String(decryptedBytes);
            System.out.println("Mensagem decriptografada: " + decryptedMessage);

            if (verifySignature(decryptedMessage)) {
                System.out.println("Hash identico!");
            }
            //pegar a chave simétrica
            System.out.println("O hash não bateu");
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
            byte[] iv = "1234567890123456".getBytes(StandardCharsets.UTF_8); // Mesmo IV fixo usado na criptografia
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKey serverSymmKey = this.serverSymmetricKey; // Obtém a chave secreta

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, serverSymmKey, ivParameterSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
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

    public boolean verifySignature(String decryptedMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decryptedMessage);
            ObjectNode objectNode = (ObjectNode) jsonNode;

            if (jsonNode.has("hash") && jsonNode.has("symmetric-key")) {
                String hash = jsonNode.get("hash").asText();
                String symmetricKeyTxt = jsonNode.get("symmetric-key").asText();

                byte[] decodedKey = Base64.getDecoder().decode(symmetricKeyTxt);

                SecretKey serverSymmKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                this.serverSymmetricKey = serverSymmKey;

                System.out.println("Hash antes de decriptografar: " + hash);
                String hashDecrypted = decryptSymmetric(hash);
                System.out.println("Hash depois de decriptografar: " + hashDecrypted);

                objectNode.remove("hash");

                String calculatedHash = calculateHash(objectNode.toString());

                byte[] decryptedHashBytes = Base64.getDecoder().decode(hashDecrypted);
                byte[] calculatedHashBytes = Base64.getDecoder().decode(calculatedHash);
                boolean isEqual = Arrays.equals(decryptedHashBytes, calculatedHashBytes);

                // Se não for igual, imprimir mais detalhes
                if (!isEqual) {
                    System.out.println("Hashes não são iguais:");
                    System.out.println("Hash decriptografado (bytes): " + Arrays.toString(decryptedHashBytes));
                    System.out.println("Hash calculado (bytes): " + Arrays.toString(calculatedHashBytes));
                }

                return isEqual;

            }
        } catch (JsonProcessingException ex) {
            System.out.println("Foi descriptografado incorretamente.");
        }

        return false;
    }

    public String decryptHash(String hash) {
        try {
            PublicKey serverPublicKey = getServerPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, serverPublicKey);

            System.out.println("Decriptando o hash...");
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(hash));

            String decryptedHash = new String(decryptedBytes);
            System.out.println("Hash decriptografado: " + decryptedHash);
            return decryptedHash;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException ex) {
            Logger.getLogger(EncryptService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String calculateHash(String message) {
        byte[] hashBytes = md.digest(message.getBytes(StandardCharsets.UTF_8));

        byte[] truncatedHash = Arrays.copyOf(hashBytes, 8);

        String base64Hash = Base64.getEncoder().encodeToString(truncatedHash);

        return base64Hash;
    }

    private void defineSecretKey(String decryptedMessage) {
        System.out.println("Definindo a simmetric key!");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decryptedMessage);

            if (jsonNode.has("symmetric-key")) {
                byte[] keyBytes = Base64.getDecoder().decode(jsonNode.get("symmetric-key").asText());
                System.out.println("Symmetric key: " + Base64.getEncoder().encodeToString(keyBytes));
                this.serverSymmetricKey = new SecretKeySpec(keyBytes, "AES");
            }

        } catch (JsonProcessingException e) {
            System.out.println("Deu erro definindo a simmetric key");
            System.out.println(e.getMessage());
        }
    }
}
