package com.analix.project.dto;

import lombok.Data;

@Data
public class UserWorkVisibilityDto {
	
	/*ユーザID*/
	private Integer userId;
	/*作業ID*/
	private Integer workId;
	/*作業名*/
	private String workName;
	/*表示設定フラグ(1:表示 0:非表示 null:未設定)*/
	private Byte isVisible;

}
