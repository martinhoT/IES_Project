package com.getaroom.app.entity;

import java.util.Date;

public abstract class Status {

    private final String room;
    private double occupacy;
    private int maxNumberOfPeople;
    private Date time;

    public Status(String room) {
        this.room = room;
    }

    public Status(String room, double occupacy, int maxNumberOfPeople, Date time) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        this.time = time;
    }

    public String getRoom() { return room; }
    public double getOccupacy() { return occupacy; }
    public int getMaxNumberOfPeople() { return maxNumberOfPeople; }
    public Date getTime() { return time; }

    public void setOccupacy(double occupacy) { this.occupacy = occupacy; }
    public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
    public void setTime(Date time) { this.time = time; }
}
