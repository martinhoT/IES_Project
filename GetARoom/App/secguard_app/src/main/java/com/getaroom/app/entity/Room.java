package com.getaroom.app.entity;

public class Room {

    private String id;

    private final String room;
    private double occupancy;
    private int maxNumberOfPeople;

    public Room(String room) {
        this.room = room;
    }

    public String getId() { return id; }
    public String getRoom() { return room; }
    public double getOccupancy() { return occupancy; }
    public int getMaxNumberOfPeople() { return maxNumberOfPeople; }

    public void setOccupancy(double occupancy) { this.occupancy = occupancy; }
    public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
}
