package com.getaroom.app.entity;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Dep {

    @Id
    String id;

    String dep;

    public Dep(String dep){
        this.dep = dep;
    }

    public void setDep(String dep){
        this.dep = dep;
    }

    public String getDep(){
        return dep;
    }

    public int getDepNumber() {
        return Integer.parseInt(dep);
    }

    public String toString() {
        return getDep();
    }
}
