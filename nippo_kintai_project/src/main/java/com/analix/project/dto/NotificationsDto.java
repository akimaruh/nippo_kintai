package com.analix.project.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
/**
 * 通知DTO
 */
@Data
public class NotificationsDto {

	//通知ID 
	Integer id;
	//タイトル
	String title;
	//本文
	String message;
	//作成日
	LocalDate createdAt;
	//通知種類
	String notificationType;
	//対象権限
	String target_role;
	//ユーザー通知DTOリスト
	List<UserNotificationsDto> userNotificationsDtoList;
	
	

}
