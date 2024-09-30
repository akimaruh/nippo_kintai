package com.analix.project.entity;

import lombok.Data;

@Data
public class Subscriptions {
	
	private Integer userId;
	private String endpoint;
	private String p256dh;
	private String auth;

}
