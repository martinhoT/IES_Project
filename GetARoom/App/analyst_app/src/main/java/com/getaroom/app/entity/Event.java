package com.getaroom.app.entity;

import java.util.Date;

public class Event {
    private String id;
    private String user;
    private String email;
    private String room;
    private boolean entered;
    private Date time;

    public Event(){}

    public Event(String id, String user, String email, String room, boolean entered, Date time) {
        this.user = user;
        this.email = email;
        this.room = room;
        this.entered = entered;
        this.time = time;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setuser(String user){
        this.user = user;
    }

    public String getuser(){
        return user;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public String getRoom(){
        return room;
    }

    public void setentered(Boolean entered){
        this.entered = entered;
    }

    public boolean getentered(){
        return entered;
    }

    public void setTime(Date time){
        this.time = time;
    }

    public Date getTime(){
        return time;
    }

    public String toString() {
        return "id: " + getId() + 
               ", user: " + getuser() +
               ", email: " + getEmail() +
               ", room: " + getRoom() +
               ", entered: " + getentered() +
               ", time: " + getTime();
    }
}
