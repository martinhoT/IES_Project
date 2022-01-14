package com.getaroom.app.entity.mongodb;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Status {

    @Id
    private String id;
    
    private String room;
    private double occupancy;
    private Date time;

    public Status() {}

    public Status(String room) {
        this.room = room;
    }

    public Status(String room, double occupancy, Date time) {
        this.room = room;
        this.occupancy = occupancy;
        this.time = time;
    }

    public String getRoom() { return room; }
    public double getOccupacy() { return occupancy; }
    public Date getTime() { return time; }

    public void setRoom(String room) { this.room = room; }
    public void setOccupacy(double occupancy) { this.occupancy = occupancy; }
    public void setTime(Date time) { this.time = time; }
}
