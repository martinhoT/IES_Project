package com.getaroom.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class History extends Event {

    @Id
    private String id;

    public History(String user, String email, String room, boolean entered, Date time) {
        super(user, email, room, entered, time);
    }

}
