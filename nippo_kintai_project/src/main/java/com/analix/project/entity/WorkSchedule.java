package com.analix.project.entity;

import java.time.LocalTime;

import lombok.Data;

@Data
public class WorkSchedule {
	
	private Integer userId;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer breakTime;

}
