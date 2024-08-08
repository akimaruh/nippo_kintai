package com.analix.project.service;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.mapper.AttendanceMapper;
import com.analix.project.mapper.MonthlyAttendanceReqMapper;
import com.analix.project.util.CustomDateUtil;

@Service
public class AttendanceService {
	@Autowired
	private AttendanceMapper attendanceMapper;
	@Autowired
	private MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;
	@Autowired
	private CustomDateUtil customDateUtil;

	/**
	 * ヘッダー:ステータス部分取得
	 * @param userId
	 * @param attendanceDate
	 * @return 月次勤怠承認テーブルのステータス
	 */
	public Integer findStatusByUserId(Integer userId, Date attendanceDate) {
		return monthlyAttendanceReqMapper.findStatusByUserIdAndYearMonth(userId, attendanceDate);
	}

	/**
	 * 一覧表示
	 * @param userId
	 * @param year
	 * @param month
	 * @return 勤怠一覧取得マッパー
	 */
	public List<DailyAttendanceForm> getFindAllDailyAttendance(Integer userId, String yearMonth) {

		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();
		List<Attendance> attendanceListSearchForUserIdAndYearMonth = attendanceMapper.findAllDailyAttendance(userId,
				yearMonth);

		//日付生成
		List<LocalDate> dateList = generateMonthDates(yearMonth);

		// Attendance情報をLocalDateでインデックス化するMapを作成
		Map<LocalDate, Attendance> attendanceMap = attendanceListSearchForUserIdAndYearMonth.stream()
				.collect(Collectors.toMap(
						attendance -> customDateUtil.convertToLocalDate(attendance.getDate()),
						attendance -> attendance));

		for (LocalDate date : dateList) {
			DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();

			dailyAttendance.setDate(date);

			// 該当する日付の出席情報を取得
			Attendance attendance = attendanceMap.get(date);

			if (attendance != null) {
				dailyAttendance.setId(attendance.getId());
				dailyAttendance.setUserId(attendance.getUserId());
				dailyAttendance.setStatus(attendance.getStatus());

				Time startTime = attendance.getStartTime();
				Time endTime = attendance.getEndTime();
				if (startTime != null) {
					String newStartTime = new SimpleDateFormat("HH:mm").format(attendance.getStartTime());
					dailyAttendance.setStartTime2(newStartTime);
				}

				if (endTime != null) {
					String newEndTime = new SimpleDateFormat("HH:mm").format(attendance.getEndTime());
					dailyAttendance.setEndTime2(newEndTime);
				}

				dailyAttendance.setRemarks(attendance.getRemarks());
			}

			dailyAttendanceList.add(dailyAttendance);
		}

		return dailyAttendanceList;
	}

	/**
	 * 承認申請ボタン活性化
	 * @param dailyAttendanceList
	 * @return 全勤怠が登録されている場合true,そうでない場合false
	 */
	public Boolean applicableCheck(List<DailyAttendanceForm> dailyAttendanceList) {

		return dailyAttendanceList.stream().allMatch(dailyAttendance -> dailyAttendance.getStatus() != null);

	}

	/**
	 *「承認申請」ボタン押下後
	 * @Param userId
	 * @Param attendanceDate
	 * @return メッセージ
	 */
	public String insertMonthlyAttendanceReq(Integer userId, Date attendanceDate) {

		Integer applicationStatus = monthlyAttendanceReqMapper.findStatusByUserIdAndYearMonth(userId, attendanceDate);

		if (applicationStatus == null) {
			MonthlyAttendanceReqDto monthlyDto = new MonthlyAttendanceReqDto();
			monthlyDto.setUserId(userId);
			monthlyDto.setTargetYearMonth(attendanceDate);
			monthlyDto.setDate(java.sql.Date.valueOf(LocalDate.now()));
			monthlyDto.setStatus(1);

			monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyDto);

		} else if (applicationStatus == 3) {
			// statusを1(承認待ち)に更新
			monthlyAttendanceReqMapper.updateStatusWaiting(userId, attendanceDate);
		}

		return "承認申請が完了しました。";
	}

	/**
	 * 承認申請取得
	 * @return 承認申請リスト
	 */
	public List<MonthlyAttendanceReqDto> getMonthlyAttendanceReq() {
		return monthlyAttendanceReqMapper.findAllMonthlyAttendanceReq();
	}

	/**
	 * status更新 承認・却下
	 * @param userId
	 * @param targetYearMonth
	 * @return メッセージ
	 */
	public String updateStatusApprove(Integer userId, String targetYearMonth) {
		monthlyAttendanceReqMapper.updateStatusApprove(userId, targetYearMonth);
		MonthlyAttendanceReqDto monthlyAttendanceReqList = monthlyAttendanceReqMapper
				.findMonthlyAttendanceReqByUserId(userId, targetYearMonth);
		String userName = monthlyAttendanceReqList.getName();
		Date date = monthlyAttendanceReqList.getDate();

		return userName + "の" + date + "における承認申請が承認されました。";
	}

	public String updateStatusReject(Integer userId, String targetYearMonth) {
		monthlyAttendanceReqMapper.updateStatusReject(userId, targetYearMonth);
		MonthlyAttendanceReqDto monthlyAttendanceReqList = monthlyAttendanceReqMapper
				.findMonthlyAttendanceReqByUserId(userId, targetYearMonth);
		String userName = monthlyAttendanceReqList.getName();
		Date date = monthlyAttendanceReqList.getDate();

		return userName + "の" + date + "における承認申請が却下されました。";
	}

	/**
	 * 承認申請者情報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return 承認申請者の勤怠リスト
	 */
	public List<Attendance> findByUserIdAndYearMonth(Integer userId, String targetYearMonth) {
		return attendanceMapper.findAllDailyAttendance(userId, targetYearMonth);
	}

	/**
	 * 勤怠登録前入力チェック
	 * @param attendanceFormList
	 * @param result
	 * @return 
	 */
	public void validationForm(AttendanceFormList attendanceFormList, BindingResult result) {

		int i = 0;
		for (DailyAttendanceForm dailyAttendanceForm : attendanceFormList.getAttendanceFormList()) {

			String startTime = dailyAttendanceForm.getStartTime2();
			String endTime = dailyAttendanceForm.getEndTime2();
			String remarks = dailyAttendanceForm.getRemarks();

			//出勤時刻の形式が不正な場合
			if (startTime != "") {
				// HH:mm形式の時刻を検出する正規表現
				String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

				// パターンをコンパイル
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(startTime);
				if (!matcher.find()) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
									"HH:mm形式で入力して下さい"));

				}
			}
			//退勤時刻の形式が不正な場合
			if (endTime != "") {
				String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

				// パターンをコンパイル
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(endTime);
				if (!matcher.find()) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
									"HH:mm形式で入力して下さい"));
					break;
				}
			}

			//出勤時間または退勤時間のどちらかが空白の場合
			if (endTime == "" && startTime != "") {
				result.addError(
						new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
								"退勤時間を入力して下さい"));
			}
			if (startTime == "" && endTime != "") {
				result.addError(
						new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
								"出勤時間を入力して下さい"));
			}

			//出勤時間＞退勤時間の場合
			if (startTime != "" && endTime != "") {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
				if (startTime.matches("\\d{1}:\\d{2}")) {
					startTime = "0" + startTime;
				}
				if (endTime.matches("\\d{1}:\\d{2}")) {
					endTime = "0" + endTime;
				}

				LocalTime startInputTime = LocalTime.parse(startTime, formatter);
				LocalTime endInputTime = LocalTime.parse(endTime, formatter);
				if (startInputTime.isAfter(endInputTime)) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
									"出勤時間は退勤時間より先になるように入力して下さい"));
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
									"退勤時間は出勤時間より後になるように入力して下さい"));
				}

			}
			//備考欄
			if (remarks != "") {
				String regex = "[^\\x00-\\x7F]";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(remarks);
				if (remarks.length() >= 20) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].remarks",
									"20字以内で入力して下さい"));

				}
				if (!matcher.find()) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].remarks",
									"全角で入力して下さい"));
				}
			}
			i++;
		}
	}

	/**
	 * 勤怠登録
	 * @param userId
	 * @param attendanceFormList
	 * @return メッセージ
	 */
	public String getRegistDailyAttendance(Integer userId, AttendanceFormList attendanceFormList) {

		List<Attendance> registAttendanceList = new ArrayList<Attendance>();

		for (DailyAttendanceForm dailyAttendanceForm : attendanceFormList.getAttendanceFormList()) {

			//LocalDate型→java.sql.Date型
			//String型→java.sql.Time型に変換
			Byte status = dailyAttendanceForm.getStatus();
			Integer registedUserId = dailyAttendanceForm.getUserId();
			LocalDate date = dailyAttendanceForm.getDate();
			String endTime = dailyAttendanceForm.getEndTime2();
			String startTime = dailyAttendanceForm.getStartTime2();

			Date convertedDate = java.sql.Date.valueOf(date);

			if (endTime != null && !endTime.trim().isEmpty()) {

				if (startTime != null && !startTime.trim().isEmpty()) {
					try {

						// 入力が "H:MM" 形式の場合、先頭に "0" を追加して "HH:MM" 形式に変換
						if (startTime.matches("\\d{1}:\\d{2}")) {
							startTime = "0" + startTime;
						}
						// 入力が "HH:MM" 形式の場合、秒を追加して "HH:MM:SS" 形式に変換
						if (startTime.matches("\\d{2}:\\d{2}")) {
							startTime = startTime + ":00";
						}
						Time convertedStartTime = java.sql.Time.valueOf(startTime);
						dailyAttendanceForm.setStartTime(convertedStartTime);
					} catch (IllegalArgumentException e) {
						// 無効なフォーマットの場合のエラーハンドリング

					}
				}
				if (endTime != null && !endTime.trim().isEmpty()) {
					try {
						// 入力が "H:MM" 形式の場合、先頭に "0" を追加して "HH:MM" 形式に変換
						if (endTime.matches("\\d{1}:\\d{2}")) {
							endTime = "0" + endTime;
						}

						// 入力が "HH:MM" 形式の場合、秒を追加して "HH:MM:SS" 形式に変換
						if (endTime.matches("\\d{2}:\\d{2}")) {
							endTime = endTime + ":00";
						}

						Time convertedEndTime = java.sql.Time.valueOf(endTime);
						dailyAttendanceForm.setEndTime(convertedEndTime);
					} catch (IllegalArgumentException e) {
						// 無効なフォーマットの場合のエラーハンドリング

					}
				}
			}

			Attendance registAttendance = new Attendance();

			registAttendance.setUserId(userId);
			registAttendance.setDate(convertedDate);
			registAttendance.setStatus(status);
			registAttendance.setRemarks(dailyAttendanceForm.getRemarks());
			registAttendance.setStartTime(dailyAttendanceForm.getStartTime());
			registAttendance.setEndTime(dailyAttendanceForm.getEndTime());

			if (status != null) {
				if (registedUserId == null) {

					registAttendanceList.add(registAttendance);
					attendanceMapper.registDailyAttendance(registAttendance);

				} else {

					registAttendance.setId(dailyAttendanceForm.getId());
					registAttendanceList.add(registAttendance);

					attendanceMapper.updateDailyAttendance(registAttendance);

				}
			}

		}
		return "勤怠登録が完了しました。"; //messageを出力するので保留

	}

	/**
	 * 表示日付生成
	 * @param yearMonth
	 * @return 1か月の日付リスト
	 */
	public List<LocalDate> generateMonthDates(String yearMonth) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start;
		try {

			// yearMonthに "-01" を追加して月の最初の日を表現
			start = LocalDate.parse(yearMonth + "-01", formatter);

		} catch (DateTimeParseException e) {

			e.printStackTrace();
			return new ArrayList<>(); // パースに失敗した場合は空のリストを返す
		}

		LocalDate end = start.plusMonths(1).minusDays(1);

		List<LocalDate> dates = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			dates.add(date);
		}

		return dates;
	}

}
