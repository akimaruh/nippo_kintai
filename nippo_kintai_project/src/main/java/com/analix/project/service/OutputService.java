package com.analix.project.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.analix.project.dto.DailyReportDetailDto;
import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.dto.MonthlyDailyReportDto;
import com.analix.project.dto.WorkbookDto;
import com.analix.project.entity.Users;
import com.analix.project.mapper.AttendanceMapper;
import com.analix.project.mapper.DailyReportMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.ExcelUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class OutputService {
	
	private final UserMapper userMapper;
	private final ExcelUtil excelUtil;
	private final DailyReportMapper dailyReportMapper;
	private final AttendanceMapper attendanceMapper;

	/**
	 * 選択したユーザーの表示情報を取得
	 * @param userId
	 * @param targetYearMonth
	 * @param session
	 * @return ユーザーID,ユーザー名,部署名
	 */
	public Users getselectedUserData(Integer userId) {
		Users selectedUserData = userMapper.findUserDataForDisplay(userId);
		return selectedUserData;

	}

	/**
	 * ワークブックDTOにデータをセットしExcel出力
	 * @param response
	 * @param session
	 * @return 反映結果
	 * @throws IOException
	 */
	public void downloadExcel(HttpServletResponse response, HttpSession session) throws IOException {

		List<DailyReportDto> dailyReportDtoList = (List<DailyReportDto>) session.getAttribute("dailyReportDtoList");
		DailyReportSummaryDto dailyReportSummaryDto = (DailyReportSummaryDto) session
				.getAttribute("dailyReportSummaryDto");

		LinkedHashMap<String, Integer> linkedHashMap = dailyReportSummaryDto.getWorkTimeByProcessMapList();
		linkedHashMap.put("総作業時間", dailyReportSummaryDto.getTimePerMonth());
		Object[] workNameArray = linkedHashMap.keySet().toArray();
		Object[] workTimeArray = linkedHashMap.values().toArray();
		//Excel用に配列に詰めなおし
		String[] dailyReportHeaderNameArray = Constants.DAILY_REPORT_HEADERNAME_ARRAY;
		String[][] dailyReportBodyArray = convertForDailyReportBodyArray(dailyReportDtoList);

		WorkbookDto workbookDto = getWorkbookDto(workNameArray, workTimeArray, dailyReportHeaderNameArray,
				dailyReportBodyArray, session);
		ExcelUtil.downloadBook(workbookDto, response);

	}

	/**
	 * ワークブック作成
	 * @param workNameArray
	 * @param workTimeArray
	 * @param dailyReportHeaderNameArray
	 * @param dailyReportBodyArray
	 * @param session
	 * @return ワークブック・ワークブック名格納後DTO
	 * @throws IOException
	 */
	private WorkbookDto getWorkbookDto(Object[] workNameArray, Object[] workTimeArray,
			Object[] dailyReportHeaderNameArray,
			Object[][] dailyReportBodyArray, HttpSession session) throws IOException {

		String outputName = Constants.CODE_VAL_DAILY_REPORT_OUTPUT;
		String sheetName = outputName;
		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		WorkbookDto workbookDto = new WorkbookDto();
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(sheetName);

		excelUtil.setHeaderRow(workbook, userData, targetYearMonth, sheetName);
		excelUtil.setRow(dailyReportHeaderNameArray, dailyReportBodyArray,workbook, sheetName);
		excelUtil.setRow(workNameArray,workTimeArray, workbook, sheetName);
		

		workbookDto.setWb(workbook);
		workbookDto.setWbName(excelUtil.createWorkbookName(outputName, targetYearMonth, userData));

		return workbookDto;

	}

	/**
	 * 日報データをExcel用の配列に変換
	 * @param dailyReportDtoList
	 * @return 変換後配列
	 */
	private String[][] convertForDailyReportBodyArray(List<DailyReportDto> dailyReportDtoList) {
		ArrayList<ArrayList<String>> dailyReportBodyArrayList = new ArrayList<>();
		for (DailyReportDto dailyReportDto : dailyReportDtoList) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
			ArrayList<String> dailyReportBodyArray = new ArrayList<>();
			dailyReportBodyArray.add(dailyReportDto.getDate().format(formatter));
			dailyReportBodyArray
					.add(dailyReportDto.getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPAN));

			for (int i = 0; i < Constants.DISPLAY_WORK_QTY; i++) {

				if (i < dailyReportDto.getDailyReportDetailDtoList().size()) {
					DailyReportDetailDto dailyReportDetailDto = dailyReportDto.getDailyReportDetailDtoList().get(i);
					dailyReportBodyArray.add(dailyReportDetailDto.getWorkName());
					dailyReportBodyArray.add(dailyReportDetailDto.getTime().toString());
				} else {
					dailyReportBodyArray.add("");
					dailyReportBodyArray.add("");
				}
			}
			dailyReportBodyArray.add(dailyReportDto.getTimePerDay().toString());
			dailyReportBodyArrayList.add(dailyReportBodyArray);
		}
		// 2次元配列に変換
		String[][] bodyArray = dailyReportBodyArrayList.stream()
				.map(u -> u.toArray(new String[0]))
				.toArray(String[][]::new);

		return bodyArray;
	}
	
	/**
	 * 帳票出力用１か月の日報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public MonthlyDailyReportDto getDailyReportListForOutput(Integer userId, YearMonth targetYearMonth) {
		List<DailyReportDto> dailyReportDtoList = dailyReportMapper.dailyReportListForAMonth(userId, targetYearMonth);
		
		if(dailyReportDtoList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> timePerDayMapList = dailyReportMapper.getTimePerDate(userId, targetYearMonth);
		//1日の作業時間取得
		Map<LocalDate, Integer> timeMap = timePerDayMapList.stream()
				.collect(Collectors.toMap(
						map -> LocalDate.parse(map.get("date").toString()), // dateをLocalDateに変換
						map -> ((BigDecimal) map.get("total_time")).intValue(), // total_timeをIntegerとして取得
						Integer::sum // 重複キーの合計を取る
				));
		// dailyReportDtoListにStreamを使って１日の作業時間を統合処理
		List<DailyReportDto> timePerDayIntegratedDailyReportList = dailyReportDtoList.stream()
				.peek(report -> {
					// 日付に基づいて作業時間を設定
					Integer workTime = timeMap.get(report.getDate());
					if (workTime != null) {
						report.setTimePerDay(workTime);
					}
				})
				.collect(Collectors.toList()); // 統合リストを作成
		
		MonthlyDailyReportDto monthlyDailyReportDto = new MonthlyDailyReportDto();
		//1カ月の日報リスト(統合済み)を追加
		monthlyDailyReportDto.setDailyReportDtoList(timePerDayIntegratedDailyReportList);
		//サマリ結果を追加
		monthlyDailyReportDto.setDailyReportSummaryDto(getMonthlyDailyReportSummary(userId,targetYearMonth));  
		
		return monthlyDailyReportDto;

	}

	/**
	 * 帳票出力用１か月の総作業時間取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public DailyReportSummaryDto getMonthlyDailyReportSummary(Integer userId, YearMonth targetYearMonth) {
		DailyReportSummaryDto dailyReportSummaryDto = new DailyReportSummaryDto();
		dailyReportSummaryDto.setTimePerMonth(dailyReportMapper.getTimePerMonth(userId, targetYearMonth));
		List<Map<String, Object>> workTimeByProcessMapList = dailyReportMapper.getWorkTimeByProcess(userId,
				targetYearMonth);
		LinkedHashMap<String, Integer> workTimeByProcessMap = workTimeByProcessMapList.stream()
				.collect(Collectors.toMap(
						map -> map.get("workName").toString(),
						map -> ((BigDecimal) map.get("time")).intValue(),
						Integer::sum, LinkedHashMap::new));
		dailyReportSummaryDto.setWorkTimeByProcessMapList(workTimeByProcessMap);
		
		return dailyReportSummaryDto;

	}

}
