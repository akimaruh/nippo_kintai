package com.analix.project.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.analix.project.Builder.ExcelReportBuilder;
import com.analix.project.dto.AttendanceSummaryDto;
import com.analix.project.dto.DailyAttendanceDto;
import com.analix.project.dto.DailyReportDetailDto;
import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.dto.MonthlyAttendanceDto;
import com.analix.project.dto.MonthlyDailyReportDto;
import com.analix.project.dto.WorkbookDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.Users;
import com.analix.project.mapper.AttendanceMapper;
import com.analix.project.mapper.DailyReportMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.AttendanceUtil;
import com.analix.project.util.Constants;
import com.analix.project.util.ExcelUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OutputService {
	private final AttendanceUtil attendanceUtil;
	private final UserMapper userMapper;
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
	 * (日報帳票)ワークブックDTOにデータをセット
	 * @param response
	 * @param session
	 * @return 反映結果
	 * @throws IOException
	 */
	public void dailyReportExcelSetAndOutput(HttpServletResponse response, HttpSession session) throws IOException {

		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		String outputName = Constants.CODE_VAL_DAILY_REPORT_OUTPUT;

		MonthlyDailyReportDto monthlyDailyReport = (MonthlyDailyReportDto) session.getAttribute("monthlyDailyReport");
		List<DailyReportDto> dailyReportDtoList = monthlyDailyReport.getDailyReportDtoList();
		DailyReportSummaryDto dailyReportSummaryDto = monthlyDailyReport.getDailyReportSummaryDto();

		LinkedHashMap<String, Integer> linkedHashMap = dailyReportSummaryDto.getWorkTimeByProcessMapList();
		linkedHashMap.put("総作業時間", dailyReportSummaryDto.getTimePerMonth());
		Object[] workNameArray = linkedHashMap.keySet().toArray();
		Object[] workTimeArray = linkedHashMap.values().toArray();
		//Excel用に配列に詰めなおし
		String[] dailyReportHeaderNameArray = Constants.DAILY_REPORT_HEADERNAME_ARRAY;
		String[][] dailyReportBodyArray = convertForDailyReportBodyArray(dailyReportDtoList);

		Workbook workbook = new ExcelReportBuilder(outputName)
				.setHeaderRow(userData, targetYearMonth)
				.addRows(dailyReportHeaderNameArray, dailyReportBodyArray)
				.addRows(workNameArray, workTimeArray)
				.addDecorations()
				.build();

		setDataToWorkbookDto(workbook, outputName, userData, targetYearMonth, response);

	}

	public void attendanceExcelSetAndOutput(HttpServletResponse response, HttpSession session) throws IOException {

		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		String outputName = Constants.CODE_VAL_ATTENDANCE_OUTPUT;

		MonthlyAttendanceDto monthlyAttendance = (MonthlyAttendanceDto) session.getAttribute("monthlyAttendance");
		AttendanceSummaryDto attendanceSummary = monthlyAttendance.getAttendanceSummaryDto();

		LinkedHashMap<String, Integer> workStatusDaysMap = monthlyAttendance.getAttendanceSummaryDto()
				.getWorkStatusDaysMap();
		LinkedHashMap<String, String> workStatusTimesMap = monthlyAttendance.getAttendanceSummaryDto()
				.getWorkStatusTimesMap();
		LinkedHashMap<String, String> getOvertimeMap = monthlyAttendance.getAttendanceSummaryDto()
				.getOvertimeOnWeekMap();
		getOvertimeMap.putFirst(Constants.TOTAL_OVERTIME,
				monthlyAttendance.getAttendanceSummaryDto().getMonthlyOverWork());
		//総労働
		String[] attendanceSummaryHeadernameArray = Constants.ATTENDANCE_SUMMARY_HEADERNAME_ARRAY;
		String[] attendanceSummaryBodyArray = { attendanceSummary.getMonthlyWorkDays().toString(),
				attendanceSummary.getMonthlyWorkTime().toString() };
		//勤怠状況別
		Object[] statusNameArray = AttendanceUtil.attendanceStatusForOutput.values().toArray();
		Object[] workStatusDaysArray = workStatusDaysMap.values().toArray();
		Object[] workStatusTimesArray = workStatusTimesMap.values().toArray();
		Object[][] workStatusBodyArray = { workStatusDaysArray, workStatusTimesArray };
		System.out.println(workStatusBodyArray + ")" + Arrays.deepToString(workStatusBodyArray));
		//残業
		Object[] overtimeHeaderArray = getOvertimeMap.keySet().toArray();
		Object[] overtimeBodyArray = getOvertimeMap.values().toArray();
		//勤怠表
		Object[] attendanceHeaderNameArray = Constants.ATTENDANCE_HEADERNAME_ARRAY;
		Object[][] attendanceBodyArray = convertForAttendanceBodyArray(monthlyAttendance.getDailyAttendanceDtoList());
		System.out.println("attendanceBodyArray:" + Arrays.deepToString(attendanceBodyArray));
		Workbook workbook = new ExcelReportBuilder(outputName)
				.setHeaderRow(userData, targetYearMonth)
				.addRows(attendanceSummaryHeadernameArray, attendanceSummaryBodyArray)
				.addRows(statusNameArray, workStatusBodyArray)
				.addRows(overtimeHeaderArray, overtimeBodyArray)
				.addRows(attendanceHeaderNameArray, attendanceBodyArray)
				.addDecorations()
				.build();

		setDataToWorkbookDto(workbook, outputName, userData, targetYearMonth, response);

	}

	public String[][] convertForAttendanceBodyArray(List<DailyAttendanceDto> dailyAttendanceDtoList) {
		ArrayList<ArrayList<String>> dailyAttendanceBodyArrayList = new ArrayList<>();
		for (DailyAttendanceDto dailyAttendance : dailyAttendanceDtoList) {
			System.out.println(dailyAttendance);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
			ArrayList<String> dailyAttendanceBodyArray = new ArrayList<>();
			dailyAttendanceBodyArray.add(dailyAttendance.getDate().format(formatter));
			dailyAttendanceBodyArray
					.add(dailyAttendance.getDate().getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPAN));

			dailyAttendanceBodyArray.add(dailyAttendance.getStatusName());
			dailyAttendanceBodyArray
					.add(dailyAttendance.getStartTime() == null ? "" : dailyAttendance.getStartTime().toString());
			dailyAttendanceBodyArray
					.add(dailyAttendance.getEndTime() == null ? "" : dailyAttendance.getEndTime().toString());
			dailyAttendanceBodyArray.add(dailyAttendance.getRemarks() == null ? "" : dailyAttendance.getRemarks());
			dailyAttendanceBodyArrayList.add(dailyAttendanceBodyArray);
		}
		String[][] bodyArray = dailyAttendanceBodyArrayList.stream().map(u -> u.toArray(new String[0]))
				.toArray(String[][]::new);

		return bodyArray;
	}

	/**
	 * (日報帳票)データをExcel用の配列に変換
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
	 * (共通)エクセル作成・ダウンロード
	 * @param workbook
	 * @param outputName
	 * @param userData
	 * @param targetYearMonth
	 * @param response
	 * @throws IOException
	 */
	public void setDataToWorkbookDto(Workbook workbook, String outputName, Users userData, YearMonth targetYearMonth,
			HttpServletResponse response) throws IOException {

		WorkbookDto workbookDto = new WorkbookDto();
		workbookDto.setWb(workbook);
		workbookDto.setWbName(outputName);
		workbookDto.setUserData(userData);
		workbookDto.setTargetYearMonth(targetYearMonth);
		ExcelUtil.downloadBook(workbookDto, response);
	}

	/**
	 * 日報帳票出力用１か月の日報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public MonthlyDailyReportDto getDailyReportListForOutput(Integer userId, YearMonth targetYearMonth) {
		List<DailyReportDto> dailyReportDtoList = dailyReportMapper.dailyReportListForAMonth(userId, targetYearMonth);
		if (dailyReportDtoList.isEmpty()) {
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
		monthlyDailyReportDto.setDailyReportSummaryDto(getMonthlyDailyReportSummary(userId, targetYearMonth));
		return monthlyDailyReportDto;

	}

	/**
	 * 日報帳票出力用１か月の総作業時間取得
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

	/**
	 * 勤怠帳票出力用１か月の勤怠作成
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public MonthlyAttendanceDto createAttendanceOutput(Integer userId, YearMonth targetYearMonth) {
		List<Attendance> attendanceList = attendanceMapper.findAllDailyAttendance(userId, targetYearMonth);
		System.out.println(attendanceList);
		if (attendanceList.isEmpty()) {
			return null;
		}
		//DTOのofメソッドを利用してエンティティからDTOへ詰め替え
		List<DailyAttendanceDto> attendanceDtoList = attendanceList.stream().map(DailyAttendanceDto::of)
				.collect(Collectors.toList());
		MonthlyAttendanceDto monthlyAttendanceDto = new MonthlyAttendanceDto();
		monthlyAttendanceDto.setDailyAttendanceDtoList(attendanceDtoList);
		monthlyAttendanceDto.setAttendanceSummaryDto(createAttendanceSummary(userId, targetYearMonth));
		System.out.println(monthlyAttendanceDto);
		return monthlyAttendanceDto;
	}

	/**
	 * 勤怠帳票サマリ作成
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public AttendanceSummaryDto createAttendanceSummary(Integer userId, YearMonth targetYearMonth) {

		//労働集計
		//		List<Map<String, Object>> retrieveWorkStatusDataList = ;
		AttendanceSummaryDto workTimeSummary = summarizeWorkTimeAndDays(
				attendanceMapper.daysTimeRetrieveByStatus(userId,
						targetYearMonth));

		//残業集計
		List<Map<String, Object>> everyWeekOvertimeHoursMapList = attendanceMapper
				.everyWeekOvertimeHoursRetrieve(userId, targetYearMonth);
		AttendanceSummaryDto overtimeSummary = summarizeOvertime(everyWeekOvertimeHoursMapList);
		//集計をDTOに格納
		AttendanceSummaryDto attendanceSummaryDto = AttendanceSummaryDto.of(workTimeSummary, overtimeSummary);
		return attendanceSummaryDto;
	}

	/**
	 * 労働時間・日数集計
	 * @param retrieveWorkStatusDataList
	 * @return
	 */
	private AttendanceSummaryDto summarizeWorkTimeAndDays(List<Map<String, Object>> retrieveWorkStatusDataList) {
		System.out.println(retrieveWorkStatusDataList);
		//総労働時間
		Duration monthlyWorkTimeDuration = Duration.ZERO;
		//総労働日数
		Integer totalWorkDays = 0;
		//勤怠状況別時間・日数
		LinkedHashMap<String, String> workStatusTimesMap = new LinkedHashMap<>();
		LinkedHashMap<String, Integer> workStatusDaysMap = new LinkedHashMap<>();
		Map<Byte, String> statusMap = AttendanceUtil.attendanceStatusForOutput;
		for (String status : statusMap.values()) {
			workStatusTimesMap.put(status, null);
			workStatusDaysMap.put(status, null);
		}

		for (Map<String, Object> retrieveWorkStatusData : retrieveWorkStatusDataList) {
			Byte workStatus = ((Integer) retrieveWorkStatusData.get("status")).byteValue();
			Integer totalTimeMinutes = 0;
			//出勤系ステータスかチェック 
			boolean isAttendanceSystem = AttendanceUtil.getAttendanceSystem().contains(workStatus);
			if (isAttendanceSystem) {
				totalTimeMinutes = ((BigDecimal) retrieveWorkStatusData.get("totalTimeMinutes")).intValue();
			}
			Integer totalDays = ((Long) retrieveWorkStatusData.get("totalDays")).intValue();
			System.out.println("該当日：" + totalDays);
			Duration totalTimeMinutesDuration = Duration.ofMinutes(totalTimeMinutes);
			String formattedTotalTime = formatDuration(totalTimeMinutesDuration);
			workStatusTimesMap.put(AttendanceUtil.getAttendanceStatus(workStatus), formattedTotalTime);
			workStatusDaysMap.put(AttendanceUtil.getAttendanceStatus(workStatus), totalDays);

			//出勤日数・時間カウント
			if (isAttendanceSystem) {
				totalWorkDays += totalDays;
				monthlyWorkTimeDuration = monthlyWorkTimeDuration.plus(totalTimeMinutesDuration);
			}
		}

		//総労働時間フォーマット
		String monthlyWorkTime = formatDuration(monthlyWorkTimeDuration);

		AttendanceSummaryDto attendanceSummaryDto = new AttendanceSummaryDto();
		attendanceSummaryDto.setMonthlyWorkDays(totalWorkDays);
		attendanceSummaryDto.setMonthlyWorkTime(monthlyWorkTime);
		attendanceSummaryDto.setWorkStatusTimesMap(workStatusTimesMap);
		attendanceSummaryDto.setWorkStatusDaysMap(workStatusDaysMap);

		return attendanceSummaryDto;
	}

	/**
	 * 残業時間集計
	 * @param everyWeekOvertimeHoursMapList
	 * @return
	 */
	private AttendanceSummaryDto summarizeOvertime(List<Map<String, Object>> everyWeekOvertimeHoursMapList) {
		LinkedHashMap<String, String> everyWeekOvertimeHoursMap = new LinkedHashMap<>();
		System.out.println(everyWeekOvertimeHoursMapList);
		Duration monthlyDuration = Duration.ZERO;
		//週の早い順にリストを並べ替え
		everyWeekOvertimeHoursMapList.sort(Comparator.comparing(m -> ((Integer) m.get("week")).toString()));
		//上の式は以下と同じ
		//		everyWeekOvertimeHoursMapList.sort((map1, map2) -> {
		//			Integer week1 = (Integer) map1.get("week");
		//			Integer week2 = (Integer) map2.get("week");
		//			return week1.compareTo(week2);
		//		});

		//ListをMap内に格納かつ、Mapのkeyを1週目、2週目に変更
		int weekCounter = 1;
		for (Map<String, Object> weeklyData : everyWeekOvertimeHoursMapList) {

			Integer overtimeMinutes = ((BigDecimal) weeklyData.get("overtimeMinutes")).intValue();
			//残業時間が0未満だったら0に変更する
			overtimeMinutes = overtimeMinutes < 0 ? 0 : overtimeMinutes;
			//TODO:後で上記も試す
			//			Integer overtimeMinutes = (Integer) weeklyData.get("overTime"); // 分単位
			Duration weeklyDuration = Duration.ofMinutes(overtimeMinutes);

			//各週残業時間フォーマット
			String formattedOvertime = formatDuration(weeklyDuration);

			//Mapに格納かつkeyをn週目に変更
			everyWeekOvertimeHoursMap.put(weekCounter + "週目", formattedOvertime);

			//加算して総残業時間計算
			monthlyDuration = monthlyDuration.plus(weeklyDuration);
			weekCounter++;
		}

		//総残業時間をフォーマット
		String formattedMonthlyOvertime = formatDuration(monthlyDuration);

		//結果をDTOに格納
		AttendanceSummaryDto attendanceSummaryDto = new AttendanceSummaryDto();
		attendanceSummaryDto.setOvertimeOnWeekMap(everyWeekOvertimeHoursMap);
		attendanceSummaryDto.setMonthlyOverWork(formattedMonthlyOvertime);

		return attendanceSummaryDto;
	}

	/**
	 * 経過時間フォーマット変換
	 * @param duration
	 * @return  
	 */
	private static String formatDuration(Duration duration) {
		System.out.println("間隔：" + duration);
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		System.out.println(minutes);
		//分は2桁分0埋め
		String minutesZeroFill = StringUtils.leftPad(String.valueOf(minutes), 2, "0");
		return hours + ":" + minutesZeroFill;

	}
}
