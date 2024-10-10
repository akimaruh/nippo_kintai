package com.analix.project.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyAttendanceForm {
	
	private Integer id;
	private Integer userId;
	private Byte status;
	private LocalDate date;
	private String startTime;
	private String endTime;
	private String remarks;
	

	
	
	
	


}
