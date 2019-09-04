package com.example.firebasetest1.Model;

public class Token {

    private String id;
    private String token;

    public Token(String token) {
        this.token = token;
        this.id = "2";
    }

    public Token() {

    }

    public void setID(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }
    public String getID() {
        return id;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
