package com.getaroom.app.entity;

import java.util.Date;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StatusHistory extends Status {

    @Id
    private String id;

    private Date time;

    public StatusHistory(String room, double occupacy, int maxNumberOfPeople) {
        super(room, occupacy, maxNumberOfPeople);

        time = Date.from(Instant.now());
    }

    public Date getTime() { return time; }

}
