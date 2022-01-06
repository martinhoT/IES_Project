package com.getaroom.app.entity;

import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "dep_id")
	private String dep_id;

	@Column(name = "occupancy")
	private double occupancy;

	@Column(name = "maxNumberOfPeople")
	private int maxNumberOfPeople;

	public Room() {
	}

	public Room(String id, String dep_id, double occupancy, int maxNumberOfPeople) {
		this.id = id;
		this.dep_id = dep_id;
		this.occupancy = occupancy;
		this.maxNumberOfPeople = maxNumberOfPeople;
	}

	public String getId() {
		return id;
	}

	public String getDep_id() {
		return dep_id;
	}

	public double getOccupancy() {
		return occupancy;
	}

	public int getMaxNumberOfPeople() {
		return maxNumberOfPeople;
	}
}
