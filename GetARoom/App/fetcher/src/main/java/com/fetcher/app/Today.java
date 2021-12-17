package com.fetcher.app;

import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Today {

    @Id
    public String id;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public String user;
    public String email;
    public String room;
    public boolean entered;
    public Date time;

    public Today() {}

    public Today(String user, String email, String room, boolean entered, Date time) {
        this.user = user;
        this.email = email;
        this.room = room;
        this.entered = entered;
        this.time = time;
    }

    public Today(String docStr) {
        Document doc = Document.parse(docStr);
        this.user = (String) doc.get("user");
        this.email = (String) doc.get("email");
        this.room = (String) doc.get("room");
        this.entered = (Boolean) doc.get("entered");
        try {
            this.time = dateFormat.parse((String) doc.get("time"));
        } catch (ParseException e) {
            System.err.println("Failure parsing date in Today object!");
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return String.format(
                "{user: %s, email: %s, room: %s, entered: %s, time: %s}",
                user, email, room, entered, time
        );
    }
}
