package com.analix.project.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.analix.project.entity.Attendance;
import com.analix.project.util.AttendanceUtil;

import lombok.Data;

@Data
public class DailyAttendanceDto {
	private Integer id;
	private Integer userId;
	/*勤怠状況-コード*/
	private Byte status;
	/*勤怠状況-日本語*/
	private String statusName;
	private LocalDate date;
	private String remarks;
	
	private String startTimeStr;
	private String endTimeStr;
	
//	private LocalTime startTime;
//	private LocalTime endTime;

	/*エンティティからDTOへ詰め替え
	ステータスコードからステータス名に変換してセット*/
	public static DailyAttendanceDto of(Attendance attendance) {
		DailyAttendanceDto dailyAttendanceDto = new DailyAttendanceDto();
		dailyAttendanceDto.setId(attendance.getId());
		dailyAttendanceDto.setUserId(attendance.getUserId());
		dailyAttendanceDto.setStatus(attendance.getStatus());
		dailyAttendanceDto.setStatusName(AttendanceUtil.getAttendanceStatus(attendance.getStatus()));
		dailyAttendanceDto.setDate(attendance.getDate());
		dailyAttendanceDto.setRemarks(attendance.getRemarks());
		
		//フォーマット処理（LocalTime型HH:mm:ss→String型HH:mm）
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		dailyAttendanceDto.setStartTimeStr(attendance.getStartTime() != null ? attendance.getStartTime().format(timeFormatter) : "");
		dailyAttendanceDto.setEndTimeStr(attendance.getEndTime() != null ? attendance.getEndTime().format(timeFormatter) : "");
		
//		dailyAttendanceDto.setStartTime(attendance.getStartTime());
//		dailyAttendanceDto.setEndTime(attendance.getEndTime());

		return dailyAttendanceDto;
	}
}
