package com.getaroom.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Event {

    @Id
    private String id;

    private String user;
    private final String email;
    private String room;
    private boolean entered;
    private Date time;

    public Event(String user, String email, String room, boolean entered, Date time) {
        this.user = user;
        this.email = email;
        this.room = room;
        this.entered = entered;
        this.time = time;
    }

    public String getId() { return id; }
    public String getUser() { return user; }
    public String getEmail() { return email; }
    public String getRoom() { return room; }
    public boolean isEntered() { return entered; }
    public Date getTime() { return time; }

    public void setUser(String user) { this.user = user; }
    public void setRoom(String room) { this.room = room; }
    public void setEntered(boolean entered) { this.entered = entered; }
    public void setTime(Date time) { this.time = time; }

    @Override
    public String toString() {
        return String.format(
                "{user: %s, email: %s, room: %s, entered: %s, time: %s}",
                user, email, room, entered, time
        );
    }
}
