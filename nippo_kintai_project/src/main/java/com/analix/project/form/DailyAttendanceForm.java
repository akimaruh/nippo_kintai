package com.analix.project.form;

import java.sql.Time;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyAttendanceForm {
	
	private Integer id;
	private Integer userId;
	private byte status;
	private LocalDate date;
	private String date2;
	private Time startTime;
	private String startTime2; 
	private Time endTime;
	private String endTime2;
	private String remarks;
	

	
	
	
	


}
