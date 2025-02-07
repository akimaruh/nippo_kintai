package com.analix.project.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class AttendanceReportDto {
	
	/*日報詳細*/
	private List<DailyReportDetailDto> dailyReportDetailDtoList;
	
	private LocalDate date;
	private Byte status;
	private String statusName;
	private LocalTime startTime;
	private LocalTime endTime;
	
	private String startTimeStr;
	private String endTimeStr;

}
