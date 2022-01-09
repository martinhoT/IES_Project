package com.getaroom.app.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlacklistNotification {

    @Id
    private String id;

    private final String user;
    private final String email;
    private final String room;
    private final Date time;

    public BlacklistNotification(String user, String email, String room, Date time) {
        this.user = user;
        this.email = email;
        this.room = room;
        this.time = time;
    }

    public static BlacklistNotification fromEvent(Event event) {
        return new BlacklistNotification(event.getUser(), event.getEmail(), event.getRoom(), event.getTime());
    }

    public String getUser() { return user; }
    public String getEmail() { return email; }
    public String getRoom() { return room; }
    public Date getTime() { return time; }

    @Override
    public String toString() {
        return "[email=" + email + ", id=" + id + ", room=" + room + ", time=" + time + ", user="
                + user + "]";
    }
}