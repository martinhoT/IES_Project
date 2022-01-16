package com.getaroom.app.entity.mysql;

import javax.persistence.*;

@Entity
@IdClass(BlacklistId.class)
@Table(name = "blacklist")
public class Blacklist {
	@Id
	@Column(name = "email")
	private String email;

	@Id
	@Column(name = "room_id")
	private String roomId;


	protected Blacklist() {

	}

	public Blacklist(String email, String roomId) {
		this.email = email;
		this.roomId = roomId;
	}

	public String getEmail() {
		return email;
	}

	public String getRoomId() {
		return roomId;
	}
}
