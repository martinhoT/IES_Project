package com.getaroom.app.entity;

public class Status {
    private String room;
    private String occupacy;
    private Long maxNumberOfPeople;
    private Boolean restricted;

    public Status(String room, String occupacy, Long maxNumberOfPeople) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        restricted = false;
	}

    public Status(String room, String occupacy, Long maxNumberOfPeople, Boolean restricted) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        this.restricted = restricted;
	}

    public Status() {

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

    //public int getCurrentOccupacy(){return Integer.parseInt(occupacy.substring(0, 3));}


    public String getCurrentOccupacy(){
        Double currentOcuppanvy = Double.parseDouble(occupacy)*100;
        return String.valueOf(currentOcuppanvy.intValue());
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

    //public String toString() {
    //    return "Room: " + getRoom() + 
    //           ", occupacy: " + getOccupacy() +
    //           ", maxNumberOfPeople: " + getMaxNumberOfPeople();
    //}

    public String toString() {
        return "Room: " + getRoom() + 
               ", occupacy: " + getOccupacy() +
               ", maxNumberOfPeople: " + getMaxNumberOfPeople() +
               ", restricted: " + getRestricted();
    }
}
