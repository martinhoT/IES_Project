package com.getaroom.app.entity;

public class Status {

    private final String room;
    private double occupacy;
    private int maxNumberOfPeople;

    public Status(String room) {
        this.room = room;
    }

    public Status(String room, double occupacy, int maxNumberOfPeople) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public String getRoom() { return room; }
    public double getOccupacy() { return occupacy; }
    public int getMaxNumberOfPeople() { return maxNumberOfPeople; }

    public void setOccupacy(double occupacy) { this.occupacy = occupacy; }
    public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
}
