package com.getaroom.app.entity;

public class Dep {

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

    public int getDepNumber(){
        return Integer.parseInt(dep);
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
