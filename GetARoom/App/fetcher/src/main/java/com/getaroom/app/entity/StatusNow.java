package com.getaroom.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "status")
public class StatusNow extends Status {

    @Id
    private String id;

    public StatusNow(String room) { super(room); }

    public StatusHistory cloneHistory() { return new StatusHistory(this.getRoom(), this.getOccupacy(), this.getMaxNumberOfPeople(), this.getTime()); }

}
