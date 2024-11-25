package com.analix.project.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentForm {
	private Integer departmentId;
	private Byte isActive;
	
	
	// 必須入力、文字数20文字まで
	@NotBlank(message = "部署名を入力してください。", groups = { RegistDepartmentGroup.class })
	@Size(max=100, message= "部署名は100文字以内で入力してください。", groups = { RegistDepartmentGroup.class })
	private String name;
	
	// 必須入力、文字数20文字まで
	@NotBlank(message = "部署名を入力してください。", groups = { ModifyDepartmentGroup.class })
	@Size(max = 20, message = "部署名は100文字以内で入力してください。", groups = { ModifyDepartmentGroup.class })
	private String newName;
	
	private String exsistsName;
//	private String inactiveName;　（これいらないかも）
	
	// 登録用 RegistDepartmentGroup
	// 変更用 ModifyDepartmentGroup
}
