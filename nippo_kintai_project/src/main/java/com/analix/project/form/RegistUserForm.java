package com.analix.project.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;


@Data
public class RegistUserForm {

	private int id;
	private String password;
	private String name;
	private String role;
	private int departmentId;
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date startDate;

	
}
