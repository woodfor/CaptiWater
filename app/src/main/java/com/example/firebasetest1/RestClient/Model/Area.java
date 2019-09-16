package com.example.firebasetest1.RestClient.Model;

import java.util.List;

public class Area {

    private long aid;
    private String name;
    private List<Tap> taps;

    public Area() {
    }

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Tap> getTaps() {
        return taps;
    }

    public void setTaps(List<Tap> taps) {
        this.taps = taps;
    }
}
