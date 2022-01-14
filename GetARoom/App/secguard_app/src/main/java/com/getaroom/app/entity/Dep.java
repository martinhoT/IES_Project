package com.getaroom.app.entity;

public class Dep {

    int id;
    String fullName;
    String shortName;
    int floors;

    public Dep() {}

    public int getId() { return id; }
    public String getShortName() { return shortName; }
    public String getFullName() { return fullName; }
    public int getFloors() { return floors; }

    public void setId(int id) { this.id = id; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setFloors(int floors) { this.floors = floors; }
    
    public String toString() { return fullName; }
}
