package com.getaroom.app.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StatusHistory extends Status {

    @Id
    private String id;

    public StatusHistory(String room, double occupacy, int maxNumberOfPeople, Date time) {
        super(room, occupacy, maxNumberOfPeople, time);
    }

}
