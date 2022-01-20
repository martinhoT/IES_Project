package com.getaroom.app.entity.mysql;

import java.io.Serializable;

public class BlacklistId implements Serializable {
	private String email;
	private String room;

	public BlacklistId() {
	}

	public BlacklistId(String email, String room) {
		this.email = email;
		this.room = room;
	}

	public String getEmail() {
		return email;
	}

	public String getRoom() {
		return room;
	}
}
