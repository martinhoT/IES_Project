package com.getaroom.app.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(BlacklistId.class)
@Table(name = "blacklist")
public class Blacklist {
	@Id
	@Column(name = "email")
	private String email;

	@Id
	@Column(name = "room_id")
	private String room_id;


	protected Blacklist() {

	}

	public Blacklist(String email, String room_id) {
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
