package com.getaroom.app.entity.mysql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.getaroom.app.other.DepartmentInfo;

@Entity
@Table
public class Department {
    
    @Id
    private int id;

    private String fullName;
    private String shortName;
    private int floors;

    public Department() {}

    public Department(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getShortName() { return shortName; }
    public int getFloors() { return floors; }

    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setFloors(int floors) { this.floors = floors; }

    public void updateFromInfo(DepartmentInfo info) {
        fullName = info.getFullName();
        shortName = info.getShortName();
        floors = info.getFloors();
    }
}
