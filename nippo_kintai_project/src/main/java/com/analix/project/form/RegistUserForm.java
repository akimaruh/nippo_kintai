package com.analix.project.form;

import lombok.Data;

@Data
public class RegistUserForm {

	private Integer id;
	private String password;
	private String name;
	private String role;
	private Integer departmentId;
	private String departmentName;
	private String startDate;

}
