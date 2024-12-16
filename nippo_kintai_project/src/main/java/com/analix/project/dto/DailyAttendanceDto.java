package com.analix.project.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
@Data
public class DailyAttendanceDto {
	private Integer id;
	private Integer userId;
	private Byte status;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private String remarks;
}
