package com.getaroom.app.entity;

public class Dep {

    String dep;

    public Dep() {}

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
