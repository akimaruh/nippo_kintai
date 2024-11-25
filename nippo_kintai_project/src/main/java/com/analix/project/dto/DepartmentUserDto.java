package com.analix.project.dto;

import lombok.Data;

@Data
public class DepartmentUserDto {
	
	/** ユーザー名 */
	private String userName;
	/** 権限 */
	private String role;
	/** 部署名 */
	private String departmentName;
	/** アクティブフラグ */
	private Byte isActive;

}
