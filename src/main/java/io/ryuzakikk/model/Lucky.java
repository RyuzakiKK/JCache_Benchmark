package io.ryuzakikk.model;

import java.io.Serializable;

public class Lucky implements Serializable {

    private String luckyId;
    private int firstLuckyNumber;
    private int secondLuckyNumber;

    public Lucky(String luckyId, int firstLuckyNumber, int secondLuckyNumber) {
        this.luckyId = luckyId;
        this.firstLuckyNumber = firstLuckyNumber;
        this.secondLuckyNumber = secondLuckyNumber;
    }

    public String getLuckyId() {
        return this.luckyId;
    }

    public int getFirstLuckyNumber() {
        return this.firstLuckyNumber;
    }

    public int getSecondLuckyNumber() {
        return this.secondLuckyNumber;
    }

    public void setLuckyId(String luckyId) {
        this.luckyId = luckyId;
    }

    public void setFirstLuckyNumber(int firstLuckyNumber) {
        this.firstLuckyNumber = firstLuckyNumber;
    }

    public void setSecondLuckyNumber(int secondLuckyNumber) {
        this.secondLuckyNumber = secondLuckyNumber;
    }

    @Override
    public String toString() {
        return "ID: " + this.luckyId + "\nFirst: " + this.firstLuckyNumber + "\nSecond:" + this.secondLuckyNumber;
    }
}