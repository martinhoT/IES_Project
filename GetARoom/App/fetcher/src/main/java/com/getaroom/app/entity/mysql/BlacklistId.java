package com.getaroom.app.entity.mysql;

import java.io.Serializable;

public class BlacklistId implements Serializable {
	private String email;
	private String roomId;

	public BlacklistId() {
	}

	public BlacklistId(String email, String roomId) {
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
