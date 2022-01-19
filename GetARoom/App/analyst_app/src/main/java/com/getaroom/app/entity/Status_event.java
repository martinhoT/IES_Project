package com.getaroom.app.entity;

import java.util.Date;

public class Status_event {
    private String room;
    private Float occupacy;
    private Double maxNumberOfPeople;
    private Date time;

    public Status_event(){}

    public Status_event(String room, Float occupacy, Double maxNumberOfPeople, Date time) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        this.time = time;
    }

    public String getRoom() { return room; }
    public Float getOccupacy() { return occupacy; }
    public Double getMaxNumberOfPeople() { return maxNumberOfPeople; }
    public Date getTime() { return time; }

    public void setRoom(String room) { this.room = room; }
    public void setOccupacy(Float occupacy) { this.occupacy = occupacy; }
    public void setMaxNumberOfPeople(Double maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
    public void setTime(Date time) { this.time = time; }

    @Override
    public String toString() {
        return String.format(
                "{room: %s, occupacy: %s, room: %s, entered: %s, time: %s}",
                room, occupacy, room, time
        );
    }
}
