package com.analix.project.form;

import lombok.Data;

@Data
public class WorkScheduleForm {
	
	private Integer userId;
	private Integer startTimeHour;
	private Integer startTimeMinute;
	private Integer endTimeHour;
	private Integer endTimeMinute;
	private Integer breakTime;

}
