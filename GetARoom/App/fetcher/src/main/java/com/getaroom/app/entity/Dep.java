package com.getaroom.app.entity;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Dep {

    @Id
    String id;

    String dep;
    int floors;

    public Dep(String dep, int floors){
        this.dep = dep;
        this.floors = floors;
    }

    public void setDep(String dep){
        this.dep = dep;
    }

    public String getDep(){
        return dep;
    }

    public void setFloors(int floors){
        this.floors = floors;
    }

    public int getFloors(){
        return floors;
    }

    public String toString() {
        return getDep();
    }
}
