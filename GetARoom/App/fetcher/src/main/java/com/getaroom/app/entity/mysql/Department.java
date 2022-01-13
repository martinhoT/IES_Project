package com.getaroom.app.entity.mysql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Department {
    
    @Id
    private int id;

    private String fullName;
    private String shortName;

    public Department() {}

    public Department(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getShortName() { return shortName; }

    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
}
