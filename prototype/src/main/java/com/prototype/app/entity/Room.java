package com.prototype.app.entity;

public class Room {
    private String room;
    private String occupacy;
    private Long maxNumberOfPeople;
    private Boolean restricted;

    public Room(String room, String occupacy, Long maxNumberOfPeople, Boolean restricted) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        this.restricted = restricted;
	}

    public void setRoom(String room){
        this.room = room;
    }

    public String getRoom(){
        return room;
    }

    public void setOccupacy(String occupacy){
        this.occupacy = occupacy;
    }

    public String getOccupacy(){
        return occupacy;
    }

    public void setMaxNumberOfPeople(Long maxNumberOfPeople){
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Long getMaxNumberOfPeople(){
        return maxNumberOfPeople;
    }

    public void setRestricted(Boolean restricted){
        this.restricted = restricted;
    }

    public Boolean getRestricted(){
        return restricted;
    }

    public String toString() {
        return "Room: " + getRoom() + 
               ", occupacy: " + getOccupacy() +
               ", maxNumberOfPeople: " + getMaxNumberOfPeople() +
               ", restricted: " + getRestricted();
    }
}
