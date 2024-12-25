package com.analix.project.dto;

import java.time.LocalDate;
import java.time.LocalTime;

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
	private LocalTime startTime;
	private LocalTime endTime;
	private String remarks;

	/*エンティティからDTOへ詰め替え
	ステータスコードからステータス名に変換してセット*/
	public static DailyAttendanceDto of(Attendance attendance) {
		DailyAttendanceDto dailyAttendanceDto = new DailyAttendanceDto();
		dailyAttendanceDto.setId(attendance.getId());
		dailyAttendanceDto.setUserId(attendance.getUserId());
		dailyAttendanceDto.setStatus(attendance.getStatus());
		dailyAttendanceDto.setStatusName(AttendanceUtil.getAttendanceStatus(attendance.getStatus()));
		dailyAttendanceDto.setDate(attendance.getDate());
		dailyAttendanceDto.setStartTime(attendance.getStartTime());
		dailyAttendanceDto.setEndTime(attendance.getEndTime());
		dailyAttendanceDto.setRemarks(attendance.getRemarks());
		return dailyAttendanceDto;
	}
}
