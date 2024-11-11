package com.analix.project.dto;

import java.util.LinkedHashMap;

import lombok.Data;

@Data
public class DailyReportSummaryDto {
	/*1か月あたりの総作業時間*/
 private Integer timePerMonth;
 
 /*1カ月当たりの各工程での総作業時間*/
 private LinkedHashMap<String,Integer> workTimeByProcessMapList;
 
}
