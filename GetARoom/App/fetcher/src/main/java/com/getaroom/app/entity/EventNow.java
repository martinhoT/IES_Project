package com.getaroom.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "event")
public class EventNow extends Event {

    @Id
    private String id;

    public EventNow(String user, String email, String room, boolean entered, Date time) {
        super(user, email, room, entered, time);
    }

    public EventHistory cloneHistory() {
        return new EventHistory(this.getUser(), this.getEmail(), this.getRoom(), this.isEntered(), this.getTime());
    }
    
}
