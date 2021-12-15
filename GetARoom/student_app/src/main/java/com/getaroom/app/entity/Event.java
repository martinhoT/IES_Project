package com.getaroom.app.entity;

public class Event {
    private Long id;
    private String person;
    private String email;
    private String room;
    private String action;
    private String time;

    public Event(Long id, String person, String email, String room, Boolean entered, String time) {
        this.id = id;
        this.person = person;
        this.email = email;
        this.room = room;
        this.action = entered ? "Entry" : "Exit";
        this.time = time;
	}

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setPerson(String person){
        this.person = person;
    }

    public String getPerson(){
        return person;
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

    public void setAction(Boolean entered){
        this.action = entered ? "Entry" : "Exit";
    }

    public String getAction(){
        return action;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public String toString() {
        return "id: " + getId() + 
               ", person: " + getPerson() +
               ", email: " + getEmail() +
               ", room: " + getRoom() +
               ", action: " + getAction() +
               ", time: " + getTime();
    }
}