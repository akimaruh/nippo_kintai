package com.analix.project.dto;

import java.util.LinkedHashMap;

import lombok.Data;

@Data
public class AttendanceSummaryDto {

	/*労働日数*/
	private Integer monthlyWorkDays;
	/*総労働時間*/
	private String monthlyWorkTime;
	/*勤怠状況別該当時間*/
	private LinkedHashMap<String, String> workStatusTimesMap;
	/*勤怠状況別該当日数*/
	private LinkedHashMap<String, Integer> workStatusDaysMap;
	/*週別残業時間*/
	private LinkedHashMap<String, String> overtimeOnWeekMap;
	/*総残業時間*/
	private String monthlyOverWork;

	/*労働集計・残業集計格納*/
	public static AttendanceSummaryDto of(AttendanceSummaryDto workTimeSummary, AttendanceSummaryDto overtimeSummary) {
		AttendanceSummaryDto attendanceSummaryDto = new AttendanceSummaryDto();
		attendanceSummaryDto.setMonthlyWorkDays(workTimeSummary.getMonthlyWorkDays());
		attendanceSummaryDto.setMonthlyWorkTime(workTimeSummary.getMonthlyWorkTime());
		attendanceSummaryDto.setWorkStatusTimesMap(workTimeSummary.getWorkStatusTimesMap());
		attendanceSummaryDto.setWorkStatusDaysMap(workTimeSummary.getWorkStatusDaysMap());
		attendanceSummaryDto.setOvertimeOnWeekMap(overtimeSummary.getOvertimeOnWeekMap());
		attendanceSummaryDto.setMonthlyOverWork(overtimeSummary.getMonthlyOverWork());
		return attendanceSummaryDto;
	}

}
