package com.example.firebasetest1.RestClient.Model;

public class Tap {

    private long tid;
    private String name;
    private String btaddress;

    public Tap(long tid, String name, String btaddress) {
        this.tid = tid;
        this.name = name;
        this.btaddress = btaddress;
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
