package com.getaroom.entity;

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

    public String getdep(){
        return dep;
    }

    public String toString() {
        return getdep();
    }
}
