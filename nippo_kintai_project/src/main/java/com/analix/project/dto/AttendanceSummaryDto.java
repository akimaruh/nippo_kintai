package com.analix.project.dto;

import java.time.LocalTime;
import java.util.LinkedHashMap;

import lombok.Data;
@Data
public class AttendanceSummaryDto {

	private Integer monthlyWorkDays;
	private LocalTime timePerMonth; 
	private LinkedHashMap<String,Integer> workStatusTimeMapList;
}


