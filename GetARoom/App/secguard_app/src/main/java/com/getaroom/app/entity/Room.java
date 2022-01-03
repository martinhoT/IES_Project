package com.getaroom.app.entity;

public class Room {

<<<<<<< HEAD
    public Room(String room, String occupacy, Long maxNumberOfPeople, Boolean restricted) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        this.restricted = restricted;
    }
=======
    private String id;
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6

    private final String room;
    private double occupacy;
    private int maxNumberOfPeople;

    public Room(String room) {
        this.room = room;
    }

    public String getId() { return id; }
    public String getRoom() { return room; }
    public double getOccupacy() { return occupacy; }
    public int getMaxNumberOfPeople() { return maxNumberOfPeople; }

<<<<<<< HEAD
    public void setOccupacy(String occupacy){
        this.occupacy = occupacy;
    }

    public String getOccupacy(){
        return occupacy;
    }

    public int getCurrentOccupacy(){return Integer.parseInt(occupacy.substring(0, occupacy.length()-1));}

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
=======
    public void setOccupacy(double occupacy) { this.occupacy = occupacy; }
    public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
}
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6
