package com.analix.project.dto;

import lombok.Data;

@Data
public class UserSearchDto {
	
	private String keyword;
	private String employeeCode;
	private String userName;
	private String department;
	private String role;

}
