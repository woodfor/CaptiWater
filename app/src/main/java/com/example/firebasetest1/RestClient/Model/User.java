package com.example.firebasetest1.RestClient.Model;

public class User {
    private long uid;
    private String name;
    private String uuid;

    public User(String uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
