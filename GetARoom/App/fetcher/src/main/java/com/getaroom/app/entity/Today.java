package com.getaroom.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Today extends Event {

    @Id
    private String id;

    public Today(String user, String email, String room, boolean entered, Date time) {
        super(user, email, room, entered, time);
    }

    public History cloneHistory() {
        return new History(this.getUser(), this.getEmail(), this.getRoom(), this.isEntered(), this.getTime());
    }
    
}
