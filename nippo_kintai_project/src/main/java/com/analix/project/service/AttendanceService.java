package com.analix.project.service;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.mapper.AttendanceMapper;
import com.analix.project.mapper.MonthlyAttendanceReqMapper;
import com.analix.project.util.AttendanceUtil;
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
	public Integer findStatusByUserId(Integer userId, LocalDate targetYearMonthAtDay) {
		return monthlyAttendanceReqMapper.findStatusByUserIdAndYearMonth(userId, targetYearMonthAtDay);
	}

	/**
	 * 勤怠表表示
	 * @param userId
	 * @param year
	 * @param month
	 * @return 勤怠一覧取得マッパー
	 */
	public List<DailyAttendanceForm> getFindAllDailyAttendance(Integer userId, YearMonth targetYearMonth) {
		LocalDate targetYearMonthAtDay =targetYearMonth.atDay(1);
		//1か月分の日付生成
		List<LocalDate> dateList = generateMonthDates(targetYearMonthAtDay);
		
		//1か月分の日付が生成できていなかったらエラー表示をコントローラで行うためこの時点でnullを返す
		if (dateList.contains(null)) {
			return null;
		}

		//勤怠一覧の取得
		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();
		List<Attendance> attendanceListSearchForUserIdAndYearMonth = attendanceMapper.findAllDailyAttendance(userId,
				targetYearMonth);
		System.out.println(attendanceListSearchForUserIdAndYearMonth);

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
	public String insertMonthlyAttendanceReq(Integer userId, LocalDate targetYearMonthAtDay) {

		Integer applicationStatus = monthlyAttendanceReqMapper.findStatusByUserIdAndYearMonth(userId, targetYearMonthAtDay);

		if (applicationStatus == null) {
			MonthlyAttendanceReqDto monthlyDto = new MonthlyAttendanceReqDto();
			monthlyDto.setUserId(userId);
			monthlyDto.setTargetYearMonth(targetYearMonthAtDay);
			monthlyDto.setDate(LocalDate.now());
			monthlyDto.setStatus(1);

			monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyDto);

		} else if (applicationStatus == 3) {
			// statusを1(承認待ち)に更新
			monthlyAttendanceReqMapper.updateStatusWaiting(userId,targetYearMonthAtDay);
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
	 * 承認申請リスト(usersテーブルと結合)
	 * @param userId
	 * @param targetYearMonth
	 * @return 承認申請リスト
	 */
	public List<MonthlyAttendanceReqDto> getMonthlyAttendanceReqByUserId(Integer userId, LocalDate targetYearMonth) {
		return monthlyAttendanceReqMapper.findAllMonthlyAttendanceReqByUserId(userId, targetYearMonth);
	}

	/**
	 * status更新 承認
	 * @param userId
	 * @param targetYearMonth
	 * @return 承認メッセージ
	 */
	public String updateStatusApprove(Integer userId, LocalDate targetYearMonthAtDay) {
		monthlyAttendanceReqMapper.updateStatusApprove(userId, targetYearMonthAtDay);
		MonthlyAttendanceReqDto monthlyAttendanceReqList = monthlyAttendanceReqMapper
				.findMonthlyAttendanceReqByUserId(userId, targetYearMonthAtDay);
		String userName = monthlyAttendanceReqList.getName();
//		LocalDate date = monthlyAttendanceReqList.getDate();
		YearMonth targetYearMonth = YearMonth.of(targetYearMonthAtDay.getYear(), targetYearMonthAtDay.getMonthValue());

		return userName + "の" + targetYearMonth + "における承認申請が承認されました。";
	}
	
	/**
	 * status更新 却下
	 * @param userId
	 * @param targetYearMonth
	 * @return 却下メッセージ
	 */
	public String updateStatusReject(Integer userId,  LocalDate targetYearMonthAtDay) {
		monthlyAttendanceReqMapper.updateStatusReject(userId, targetYearMonthAtDay);
		MonthlyAttendanceReqDto monthlyAttendanceReqList = monthlyAttendanceReqMapper
				.findMonthlyAttendanceReqByUserId(userId, targetYearMonthAtDay);
		String userName = monthlyAttendanceReqList.getName();
//		LocalDate date = monthlyAttendanceReqList.getDate();
		YearMonth targetYearMonth = YearMonth.of(targetYearMonthAtDay.getYear(), targetYearMonthAtDay.getMonthValue());

		return userName + "の" + targetYearMonth + "における承認申請が却下されました。";
	}

	/**
	 * 承認申請者情報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return 承認申請者の勤怠リスト
	 */
	public List<Attendance> findByUserIdAndYearMonth(Integer userId, YearMonth targetYearMonth) {
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
			Byte status = dailyAttendanceForm.getStatus();
			List<Byte> attendanceSystem = AttendanceUtil.getAttendanceSystem();
			List<Byte> holidaySystem = AttendanceUtil.getHolidaySystem();

			//備考欄
			if (remarks != "") {
				String regex = "[^\\x00-\\x7F]";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(remarks);
				if (remarks.length() > 20) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].remarks",
									"20字以内で入力して下さい"));

				}
				if (!matcher.find()) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].remarks",
									"全角で入力して下さい"));
				}
				if (status == null) {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].status",
									"勤怠状況を入力して下さい"));
				}
			}
			//休日系統
			if (holidaySystem.contains(status)) {
				if (startTime != "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
									"休日に出勤時間は入力できません"));
				}
				if (endTime != "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
									"休日に退勤時間は入力できません"));
				}
				i++;
				continue;
			}

			//出勤系統
			if (attendanceSystem.contains(status)) {
				//出勤時間も退勤時間も入力していない場合(以降の入力チェックが不要のためブレイク)
				if (startTime == "" && endTime == "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
									"出勤時間を入力して下さい"));
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
									"退勤時間を入力して下さい"));
					i++;
					continue;
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
			}
			//桁数補足
			if (startTime != "" || endTime != "") {

				if (startTime.matches("\\d{1}:\\d{2}")) {
					startTime = "0" + startTime;
				}
				if (endTime.matches("\\d{1}:\\d{2}")) {
					endTime = "0" + endTime;
				}

				//DateTimeParseException eで同じことやっているのでコメントアウト
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

					}
				}

				LocalTime startInputTime;
				LocalTime endInputTime;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

				//出勤時間＞退勤時間の場合

				try {
					startInputTime = LocalTime.parse(startTime, formatter);

					try {
						endInputTime = LocalTime.parse(endTime, formatter);

						if (startInputTime.isAfter(endInputTime)) {
							result.addError(
									new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime2",
											"出勤時間は退勤時間より先になるように入力して下さい"));
							result.addError(
									new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime2",
											"退勤時間は出勤時間より後になるように入力して下さい"));
						}

					} catch (DateTimeParseException e) {

					}

				} catch (DateTimeParseException e) {

				}

			}
			if ((startTime != "" && endTime != "") && status == null) {
				result.addError(
						new FieldError("attendanceFormList", "attendanceFormList[" + i + "].status",
								"勤怠状況を入力して下さい"));

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
		return "勤怠登録が完了しました。";

	}

	/**
	 * 表示日付生成
	 * @param yearMonth
	 * @return 1か月の日付リスト
	 */
	public List<LocalDate> generateMonthDates(LocalDate targetYearMonth) {

//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = targetYearMonth;
		LocalDate end = start.plusMonths(1).minusDays(1);

		List<LocalDate> dates = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			dates.add(date);
		}

		return dates;
	}
	
	//ステータスを取得する
		public List<Users> registCheck() {
			LocalDate today = LocalDate.now();
			List<Users> unSubmitterList = attendanceMapper.attendanceUnsubmittedPersonList(today);
			System.out.println(unSubmitterList);
			return unSubmitterList;

		}

}
