package com.analix.project.entity;

import lombok.Data;

@Data
public class UserDepartmentOrder {
	
	private Integer id;
	private Integer userId;
	private Integer departmentId;
	private Integer sortOrder;

}
