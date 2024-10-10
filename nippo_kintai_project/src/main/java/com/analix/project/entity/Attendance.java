package com.analix.project.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class Attendance {

	private Integer id;
	private Integer userId;
	private Byte status;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private String remarks;

}