package com.getaroom.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person {
	@Column(name = "name")
	private String name;

	@Id
	@Column(name = "email")
	private String email;

	public Person(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public Person() {

	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
