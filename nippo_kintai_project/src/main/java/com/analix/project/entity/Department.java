package com.analix.project.entity;

public class Department {

	private Integer departmentId;
	private String name;
	private Byte isActive;
	private String newName;
	private String exsistsName;
	private String inactiveName;


	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getIsActive() {
		return isActive;
	}

	public void setIsActive(Byte isActive) {
		this.isActive = isActive;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getExsistsName() {
		return exsistsName;
	}

	public void setExsistsName(String exsistsName) {
		this.exsistsName = exsistsName;
	}

	public String getInactiveName() {
		return inactiveName;
	}

	public void setInactiveName(String inactiveName) {
		this.inactiveName = inactiveName;
	}
	
	
}
