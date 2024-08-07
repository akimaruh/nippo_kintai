package com.analix.project.entity;

import java.time.LocalDate;

public class Users {

	
	private Integer id;
	private String password;
	private String name;
	private String role;
	private Integer departmentId;

	private LocalDate startDate;
	private Integer status; // ステータスフィールドを追加

	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public Integer getDepartmentId() {
		return departmentId;
	}

	
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	
	public LocalDate getStartDate() {
		return startDate;
	}

	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
