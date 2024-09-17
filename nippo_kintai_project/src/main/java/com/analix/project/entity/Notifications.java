package com.analix.project.entity;

import java.time.LocalDate;

import lombok.Data;
/**
 * 通知エンティティ
 */
@Data
public class Notifications {
	
	//通知ID 
	Integer id;
	//タイトル
	String title;
	//本文
	String message;
	//作成日
	LocalDate createdAt;
	//表示日
	LocalDate notifiedAt;
	//ステータス
	String status;
	//通知種類
	String notificationType;
	//対象権限
	String target_role; 
	

}
