package com.analix.project.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.util.ExcelUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class OutputService {
	@Autowired
	ExcelUtil excelUtil;

	public void downloadExcel(List<DailyReportDto> dailyReportDtoList, DailyReportSummaryDto dailyReportSummaryDto,
			HttpServletResponse response) throws IOException {
//		WorkbookDto workbookDto = getWorkbookDto();
//		Map<String, Integer> map = dailyReportSummaryDto.getWorkTimeByProcessMapList();
//		Object[] workNameArray = map.keySet().toArray();
//		Object[]workTimeArray = map.values().toArray();
//		//Excel用に配列に詰めなおし
//		Object[] dailyReportHeaderNameArray = { "日付", "曜日", "作業①", "時間①", "作業②", "時間②", "作業③", "時間③", "作業④", "時間④",
//				"作業⑤", "時間⑤", "総作業時間" };
//		Object[][] dailyReportBodyArray = convertForDailyReportBodyArray(dailyReportDtoList);
//		
//		System.out.println(dailyReportBodyArray);
//		workbookDto.getWb().createSheet("日報帳票");
//		String sheetName = workbookDto.getWb().getSheetName(0);
//		Sheet a =excelUtil.setHeaderRow(workNameArray,sheetName);
//		Sheet b =excelUtil.setHeaderRow(dailyReportHeaderNameArray,sheetName);
//		Sheet c =excelUtil.setBodyRow(workTimeArray,sheetName);
//		Sheet d =excelUtil.setBodyRow(dailyReportBodyArray,sheetName);
	
		//		ExcelUtil.downloadBook(workbookDto, response);

	}

//	private WorkbookDto getWorkbookDto() throws IOException {
//		WorkbookDto workbookDto = new WorkbookDto();
//
//		return workbookDto;

//	}

//	private Object[][] convertForDailyReportBodyArray(List<DailyReportDto> dailyReportDtoList) {
//		//		Object[][] bodyArray = {};
//		ArrayList<ArrayList<String>> dailyReportBodyArrayList = new ArrayList<>();
//		for (DailyReportDto dailyReportDto : dailyReportDtoList) {
//			System.out
//					.println(dailyReportDto.getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPANESE));
//			ArrayList<String> dailyReportBodyArray = new ArrayList<>();
//			dailyReportBodyArray.add(dailyReportDto.getDate().toString());
//			dailyReportBodyArray
//					.add(dailyReportDto.getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPANESE));
//
//			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDto.getDailyReportDetailDtoList()) {
//				dailyReportBodyArray.add(dailyReportDetailDto.getWorkName());
//				dailyReportBodyArray.add(dailyReportDetailDto.getTime().toString());
//			}
//			dailyReportBodyArray.add(dailyReportDto.getTimePerDay().toString());
//			dailyReportBodyArrayList.add(dailyReportBodyArray);
//		}
//		// 2次元配列に変換
//		Object[][] bodyArray = dailyReportBodyArrayList.stream()
//				.map(u -> u.toArray(new String[0]))
//				.toArray(String[][]::new);
//
//		return bodyArray;
//	}

}
