package com.analix.project.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * ユーザー通知エンティティ
 */
@Data
public class UserNotificationsDto {

	//ユーザー通知ID
	Integer id;
	//ユーザーID
	Integer userId;
	//通知ID
	Integer notificationId;
	//ステータス
		String status;
	//確認日
	LocalDate readAt;
	

}
