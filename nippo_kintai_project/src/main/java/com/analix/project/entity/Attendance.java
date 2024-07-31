package com.analix.project.entity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Attendance {

	private Integer id;
	private Integer userId;
	private Byte status;
	private Date date;
	private LocalDate date2;
	private Time startTime;
	private LocalDate startTime2;
	private Time endTime;
	private String remarks;
	private LocalDate endTime2;

}