package com.analix.project.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TemporaryPassword {
	/*仮パスワードID*/
	private Integer id;
	/*仮パスワード*/
	private String temporaryPassword;
	/*ユーザーID*/
	private Integer userId;
	/*作成日時*/
	private LocalDateTime expirationDateTime;
	/*有効フラグ*/
	private Byte activeFlg;
}
