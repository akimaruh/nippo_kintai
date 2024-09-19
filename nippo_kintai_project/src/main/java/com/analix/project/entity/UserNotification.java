package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;

/**
 * ユーザー通知エンティティ
 */
@Data
public class UserNotification {

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
