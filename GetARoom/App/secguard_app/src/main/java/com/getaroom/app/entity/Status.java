package com.getaroom.app.entity;

public class Status {
    private String id;
    private String room;
    private Double occupancy;
    private Integer maxNumberOfPeople;
    //private Boolean restricted;

    public Status() {}

    public Status(String id, String room, Double occupancy, Integer maxNumberOfPeople) {
        this.room = room;
        this.occupancy = occupancy;
        this.maxNumberOfPeople = maxNumberOfPeople;
        //restricted = false;
	}

    // public Status(String room, String occupacy, Long maxNumberOfPeople, Boolean restricted) {
    //     this.room = room;
    //     this.occupacy = occupacy;
    //     this.maxNumberOfPeople = maxNumberOfPeople;
    //     this.restricted = restricted;
	// }


    public void setId(String id){
        this.id = id;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public String getRoom(){
        return room;
    }

    public void setOccupnacy(Double occupancy){
        this.occupancy = occupancy;
    }

    public String getId(){
        return id;
    }

    public Double getOccupancy(){
        return occupancy;
    }

    //public int getCurrentOccupacy(){return Integer.parseInt(occupacy.substring(0, 3));}


    public String getCurrentOccupancy(){
        Double currentOcuppancy = ((double) occupancy)*100;
        return String.valueOf(currentOcuppancy.intValue());
    }

    public void setMaxNumberOfPeople(Integer maxNumberOfPeople){
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Integer getMaxNumberOfPeople(){
        return maxNumberOfPeople;
    }

    // public void setRestricted(Boolean restricted){
    //     this.restricted = restricted;
    // }

    // public Boolean getRestricted(){
    //     return restricted;
    // }

    //public String toString() {
    //    return "Room: " + getRoom() + 
    //           ", occupacy: " + getOccupacy() +
    //           ", maxNumberOfPeople: " + getMaxNumberOfPeople();
    //}

    // public String toString() {
    //     return "Room: " + getRoom() + 
    //            ", occupacy: " + getOccupacy() +
    //            ", maxNumberOfPeople: " + getMaxNumberOfPeople() +
    //            ", restricted: " + getRestricted();
    // }
}
