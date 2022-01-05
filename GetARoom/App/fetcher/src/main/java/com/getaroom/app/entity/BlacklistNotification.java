package com.getaroom.app.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlacklistNotification extends Event {

    @Id
    private String id;

    public BlacklistNotification(String user, String email, String room, Date time) {
        super(user, email, room, true, time);
    }

    public static BlacklistNotification fromEvent(Event event) {
        return new BlacklistNotification(event.getUser(), event.getEmail(), event.getRoom(), event.getTime());
    }
}