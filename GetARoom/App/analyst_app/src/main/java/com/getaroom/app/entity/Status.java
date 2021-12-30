package com.getaroom.app.entity;

public class Status {
    private String id;
    private String room;
    private Double occupacy;
    private Integer maxNumberOfPeople;

    public Status() {}

    public Status(String id, String room, Double occupacy, Integer maxNumberOfPeople) {
        this.room = room;
        this.occupacy = occupacy;
        this.maxNumberOfPeople = maxNumberOfPeople;
	}


    public void setId(String id){
        this.id = id;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public String getRoom(){
        return room;
    }

    public void setOccupacy(Double occupacy){
        this.occupacy = occupacy;
    }

    public String getId(){
        return id;
    }

    public Double getOccupacy(){
        return occupacy;
    }

    //public int getCurrentOccupacy(){return Integer.parseInt(occupacy.substring(0, 3));}


    public String getCurrentOccupacy(){
        Double currentOcuppanvy = ((double) occupacy)*100;
        return String.valueOf(currentOcuppanvy.intValue());
    }

    public void setMaxNumberOfPeople(Integer maxNumberOfPeople){
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Integer getMaxNumberOfPeople(){
        return maxNumberOfPeople;
    }
}
