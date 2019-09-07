package com.example.firebasetest1.RestClient.Model;

public class Suburb {
    private long subid;
    private String name;
    private String postcode;
    private State state;

    public Suburb(String name, String postcode) {

        this.name = name;
        this.postcode = postcode;
    }

    public long getSubid() {
        return subid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
