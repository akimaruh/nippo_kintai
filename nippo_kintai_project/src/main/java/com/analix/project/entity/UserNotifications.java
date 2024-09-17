package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;

/**
 * ユーザー通知エンティティ
 */
@Data
public class UserNotifications {

	//ユーザー通知ID
	Integer id;
	//ユーザーID
	Integer userId;
	//通知ID
	Integer notificationId;
	//確認日
	LocalDate readAt;
	//権限
	String role;

}
