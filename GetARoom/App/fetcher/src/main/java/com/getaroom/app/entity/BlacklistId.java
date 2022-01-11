package com.getaroom.app.entity;

import java.io.Serializable;

public class BlacklistId implements Serializable {
	private String email;
	private String room_id;

	public BlacklistId() {
	}

	public BlacklistId(String email, String room_id) {
		this.email = email;
		this.room_id = room_id;
	}

	public String getEmail() {
		return email;
	}

	public String getRoom_id() {
		return room_id;
	}
}
