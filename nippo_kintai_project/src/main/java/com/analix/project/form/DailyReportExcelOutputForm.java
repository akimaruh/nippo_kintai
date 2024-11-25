package com.analix.project.form;

import java.time.YearMonth;
import java.util.List;

import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;

import lombok.Data;
@Data
public class DailyReportExcelOutputForm {

	private Integer userId;
	private String userName;
	private YearMonth targetYearMonth;
	private List<DailyReportDto> dailyReportDtoList;
	private DailyReportSummaryDto dailyReportSummaryDto;
	
	
}
