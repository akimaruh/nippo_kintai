package com.analix.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginUserDto {
	/*ユーザーID*/
	private Integer id;
	/*ユーザー名*/
	private String name;
	/*権限*/
	private String role;
	/*部署ID*/
	private Integer departmentId;
	/*利用開始日*/
	private LocalDate startDate;
	/*社員番号*/
	private Integer employeeCode;
	/*仮パスワード作成日時*/
	private LocalDateTime expirationDateTime;
	/*仮パスワード有効フラグ*/
	private Byte activeFlg;
}
