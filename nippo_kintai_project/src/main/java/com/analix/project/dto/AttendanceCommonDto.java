package com.analix.project.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class AttendanceCommonDto {
	
	private LocalDate date;
	private Byte status;
	private LocalTime startTime;
	private LocalTime endTime;
	private String remarks;

}
