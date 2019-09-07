package com.example.firebasetest1.RestClient.Model;

public class House {
    private long hid;
    private String name;
    private String address;
    private int nop;
    private User user;
    private Suburb suburb;

    public House(String name, String address, int nop) {
        this.name = name;
        this.address = address;
        this.nop = nop;

    }

    public long getHid() {
        return hid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNop() {
        return nop;
    }

    public void setNop(int nop) {
        this.nop = nop;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Suburb getSuburb() {
        return suburb;
    }

    public void setSuburb(Suburb suburb) {
        this.suburb = suburb;
    }


}
