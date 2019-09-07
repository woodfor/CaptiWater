package com.example.firebasetest1.RestClient.Model;

public class State {
    private long sid;
    private String name;

    public State(String name) {

        this.name = name;
    }

    public long getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
