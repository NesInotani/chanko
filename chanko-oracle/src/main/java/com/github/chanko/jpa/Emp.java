package com.github.chanko.jpa;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = "Emp.all", query = "select * from emp", resultClass = Emp.class),
		@NamedNativeQuery(name = "Emp.matchName", query = "select * from emp where name = :name", resultClass = Emp.class) })
public class Emp {
	@Id
	private long id;

	@Column
	private String name;

	@Column
	private Timestamp created;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

}
