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
	@Column(name = "room")
	private String room;


	protected Blacklist() {

	}

	public Blacklist(String email, String room) {
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
