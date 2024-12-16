package com.analix.project.dto;

import java.util.List;

import lombok.Data;
@Data
public class MonthlyAttendanceDto {
	/*１か月分の勤怠リスト*/
	List<DailyAttendanceDto> dailyAttendanceDtoList;
	/*１か月分の勤怠サマリ*/
	AttendanceSummaryDto attendanceSummaryDto;
	
}
