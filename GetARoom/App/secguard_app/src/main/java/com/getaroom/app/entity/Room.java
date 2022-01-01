package com.getaroom.app.entity;

public class Room {

    private String id;

    private final String room;
    private double occupacy;
    private int maxNumberOfPeople;

    public Room(String room) {
        this.room = room;
    }

    public String getId() { return id; }
    public String getRoom() { return room; }
    public double getOccupacy() { return occupacy; }
    public int getMaxNumberOfPeople() { return maxNumberOfPeople; }

    public void setOccupacy(double occupacy) { this.occupacy = occupacy; }
    public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
}
