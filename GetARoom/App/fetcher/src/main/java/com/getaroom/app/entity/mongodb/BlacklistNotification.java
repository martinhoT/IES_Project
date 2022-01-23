package com.getaroom.app.entity.mongodb;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlacklistNotification {

    @Id
    private String id;

    private String user;
    private String email;
    private String room;
    private Date time;
    private boolean seen;

    public BlacklistNotification(String user, String email, String room, Date time, boolean seen) {
        this.user = user;
        this.email = email;
        this.room = room;
        this.time = time;
        this.seen = seen;
    }

    public static BlacklistNotification fromEvent(Event event) {
        return new BlacklistNotification(event.getUser(), event.getEmail(), event.getRoom(), event.getTime(), false);
    }

    public String getId() { return id; }
    public String getUser() { return user; }
    public String getEmail() { return email; }
    public String getRoom() { return room; }
    public Date getTime() { return time; }
    public boolean isSeen() { return seen; }

    
    public void setId(String id) { this.id = id; }
    public void setUser(String user) { this.user = user; }
    public void setEmail(String email) { this.email = email; }
    public void setRoom(String room) { this.room = room; }
    public void setTime(Date time) { this.time = time; }
    public void setSeen(boolean seen) { this.seen = seen; }

    @Override
    public String toString() {
        return "[email=" + email + ", id=" + id + ", room=" + room + ", time=" + time + ", user="
                + user + "]";
    }
}