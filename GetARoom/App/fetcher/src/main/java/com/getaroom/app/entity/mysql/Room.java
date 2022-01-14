package com.getaroom.app.entity.mysql;

import java.util.Date;

import javax.persistence.*;

import com.getaroom.app.entity.mongodb.Status;

@Entity
@Table(name = "room")
public class Room {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "dep_id")
	private int depId;

	@Column(name = "occupancy")
	private double occupancy;

	@Column(name = "maxNumberOfPeople")
	private int maxNumberOfPeople;

	@Column(name = "time")
	private Date lastUpdateTime;

	public Room() {}

	public Room(String id, int depId) {
		this.id = id;
		this.depId = depId;
	}

	public Room(String id, int depId, double occupancy, int maxNumberOfPeople) {
		this.id = id;
		this.depId = depId;
		this.occupancy = occupancy;
		this.maxNumberOfPeople = maxNumberOfPeople;
	}

	public String getId() { return id; }
	public int getDepId() { return depId; }
	public double getOccupancy() { return occupancy; }
	public int getMaxNumberOfPeople() { return maxNumberOfPeople; }
	public Date lastUpdateTime() { return lastUpdateTime; }

	public void setId(String id) { this.id = id; }
	public void setDepId(int depId) { this.depId = depId; }
	public void setOccupancy(double occupancy) { this.occupancy = occupancy; }
	public void setMaxNumberOfPeople(int maxNumberOfPeople) { this.maxNumberOfPeople = maxNumberOfPeople; }
	public void setLastUpdateTime(Date lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

	public Status createStatus() {
		return new Status(id, occupancy, lastUpdateTime);
	}
}
