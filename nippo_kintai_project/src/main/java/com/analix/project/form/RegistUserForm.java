package com.analix.project.form;



import lombok.Data;


@Data
public class RegistUserForm {

	private Integer id;
	private String password;
	private String name;
	private String role;
	private int departmentId;
	private String startDate;

	
}
