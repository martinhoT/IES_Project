package com.getaroom.app.entity;

public class Event {
    private String id;
    private String user;
    private String email;
    private String room;
    private Boolean entered;
    private String time;

    public Event(){}

    public Event(String id, String user, String email, String room, Boolean entered, String time) {
        this.id = id;
        this.user = user;
        this.email = email;
        this.room = room;
        // this.entered = entered ? "Entry" : "Exit";
        this.entered = entered;
        this.time = time;
	}

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getUser(){
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

    public void setEntered(Boolean entered){
        // this.entered = entered ? "Entry" : "Exit";
        this.entered = entered;
    }

    public Boolean getEntered(){
        return entered;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public String toString() {
        return "id: " + getId() + 
               ", user: " + getUser() +
               ", email: " + getEmail() +
               ", room: " + getRoom() +
               ", entered: " + getEntered() +
               ", time: " + getTime();
    }
}