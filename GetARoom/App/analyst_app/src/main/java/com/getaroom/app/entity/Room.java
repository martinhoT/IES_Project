package com.getaroom.app.entity;

public class Room {
    private String id;
    private Double occupancy;
    private Integer maxNumberOfPeople;

    public Room() {}

    public Room(Double occupancy, Integer maxNumberOfPeople) {
        this.occupancy = occupancy;
        this.maxNumberOfPeople = maxNumberOfPeople;
	}

    public void setId(String id){
        this.id = id;
    }

    public void setOccupancy(Double occupancy){
        this.occupancy = occupancy;
    }

    public String getId(){
        return id;
    }

    public Double getOccupancy(){
        return occupancy;
    }

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

}
