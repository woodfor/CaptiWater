package com.example.firebasetest1.RestClient.Model;

public class Tap {

    private long tid;
    private String name;
    private String btaddress;
    private String token;

    public Tap(long tid, String name, String btaddress,String token) {
        this.tid = tid;
        this.name = name;
        this.btaddress = btaddress;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Tap(){

    }

    public long getTid() {
        return tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBtaddress() {
        return btaddress;
    }

    public void setBtaddress(String btaddress) {
        this.btaddress = btaddress;
    }
}
