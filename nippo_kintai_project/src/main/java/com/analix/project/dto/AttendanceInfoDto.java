package com.analix.project.dto;

import lombok.Data;

@Data
public class AttendanceInfoDto {
	
	private AttendanceCommonDto currentAttendance;
	private AttendanceCommonDto correctedAttendance;

	private String attendanceMessage;
	private String correctionMessage;

	private String correctionRejectionReason;
	private String correctionReason;
	private String correctionConfirmer;
	private String correctionId;

}
