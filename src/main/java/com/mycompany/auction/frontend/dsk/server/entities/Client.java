package com.mycompany.auction.frontend.dsk.server.entities;

public class Client {
    
    private String name;
    private String username;
    private String cpf;
    private String privateKey;

    public Client(String username, String name, String cpf) {
        this.username = username;
        this.name = name;
        this.cpf = cpf;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public String toString() {
        return "Client{" + "name=" + name + ", cpf=" + cpf;
    }
    
}
