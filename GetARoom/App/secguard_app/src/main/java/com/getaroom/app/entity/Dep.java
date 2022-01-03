package com.getaroom.app.entity;

<<<<<<< HEAD
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document
public class Dep {

    @Id
    String id;

    String dep;

    public Dep(String dep){
        this.dep = dep;
=======
public class Dep {

    String dep;
    int floors;

    public Dep() {}

    public Dep(String dep, int floors){
        this.dep = dep;
        this.floors = floors;
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6
    }

    public void setDep(String dep){
        this.dep = dep;
    }

<<<<<<< HEAD
    public String getdep(){
        return dep;
    }

    public String toString() {
        return getdep();
=======
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
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6
    }
}
