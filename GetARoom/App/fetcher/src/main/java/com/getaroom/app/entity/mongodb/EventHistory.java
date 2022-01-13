package com.getaroom.app.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class EventHistory extends Event {

    @Id
    private String id;

    public EventHistory(String user, String email, String room, boolean entered, Date time) {
        super(user, email, room, entered, time);
    }

}
