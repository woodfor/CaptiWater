package com.example.firebasetest1.RestClient.Model;

public class RankResult {
    private long totalLiter;
    private String houseName;

    public RankResult(long totalLiter, String houseName) {
        this.totalLiter = totalLiter;
        this.houseName = houseName;
    }

    public long getTotalLiter() {
        return totalLiter;
    }

    public void setTotalLiter(long totalLiter) {
        this.totalLiter = totalLiter;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
}
