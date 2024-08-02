package com.analix.project.form;

import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

@Valid
@Data
public class AttendanceFormList {
	private List<DailyAttendanceForm> attendanceFormList; 

}
