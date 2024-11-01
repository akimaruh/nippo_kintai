package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserWorkVisibility {
	/*ユーザー作業表示関係ID*/
	private Integer id;
	/*ユーザID*/
	private Integer userId;
	/*作業ID*/
	private Integer workId;
	/*表示設定フラグ*/
	private Byte isVisible;
	/*作成日*/
	private LocalDate createdAt;
	/*更新日*/
	private LocalDate updatedAt;
}
