package com.analix.project.service;

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

import com.analix.project.dto.AttendanceCorrectionDto;
import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.AttendanceCorrection;
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceCorrectionForm;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.mapper.AttendanceCorrectionMapper;
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
	@Autowired
	private AttendanceCorrectionMapper attendanceCorrectionMapper;


	/**
	 * ヘッダー:ステータス部分取得
	 * @param userId
	 * @param targetYearMonthAtDay
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
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
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
						attendance -> attendance.getDate(),
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

				LocalTime startTime = attendance.getStartTime();
				LocalTime endTime = attendance.getEndTime();
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
				if (startTime != null) {
					String startTimeString = startTime.format(dateTimeFormatter);
					dailyAttendance.setStartTime(startTimeString);
				}
				if (endTime != null) {
					String endTimeString = endTime.format(dateTimeFormatter);
					dailyAttendance.setEndTime(endTimeString);
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
	 * @Param targetYearMonthAtDay
	 * @return メッセージ
	 */
	public String insertMonthlyAttendanceReq(Integer userId, LocalDate targetYearMonthAtDay) {

		Integer applicationStatus = monthlyAttendanceReqMapper.findStatusByUserIdAndYearMonth(userId,
				targetYearMonthAtDay);

		if (applicationStatus == null) {
			MonthlyAttendanceReqDto monthlyDto = new MonthlyAttendanceReqDto();
			monthlyDto.setUserId(userId);
			monthlyDto.setTargetYearMonth(targetYearMonthAtDay);
			monthlyDto.setDate(LocalDate.now());
			monthlyDto.setStatus(1);

			monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyDto);

		} else if (applicationStatus == 3) {
			// statusを1(承認待ち)に更新
			monthlyAttendanceReqMapper.updateStatusWaiting(userId, targetYearMonthAtDay);
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
	 * 訂正申請取得
	 * @return 承認申請リスト
	 */
	public List<AttendanceCorrection> getAttendanceCorrection() {
		return attendanceCorrectionMapper.findAllAttendanceCorrection();
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
	 * 『月次：承認申請者』リンク押下後
	 * 月次idからデータ（ユーザーid、申請者、対象年月日、申請日）を取得
	 * @param id 月次id
	 * @return monthlyDto 月次データ
	 */
	public MonthlyAttendanceReqDto getMonthlyDataById(Integer id) {
		MonthlyAttendanceReqDto monthlyDto = monthlyAttendanceReqMapper.findMonthlyDataById(id);
		// 日付のフォーマット処理
		YearMonth formattedYearMonth = YearMonth.from(monthlyDto.getTargetYearMonth());
		String yearMonthStr = formattedYearMonth.format(DateTimeFormatter.ofPattern("yyyy/MM"));
		String formattedDate = customDateUtil.dateHyphenSlashConverter(monthlyDto.getDate());
		// フォーマット済みの値をDtoにセット
		monthlyDto.setFormattedYearMonth(formattedYearMonth); // YearMonth型（yyyy-MM）
		monthlyDto.setYearMonthStr(yearMonthStr); // String型（yyyy/MM）
		monthlyDto.setFormattedDate(formattedDate); // String型（yyyy/MM/dd）
		
		return monthlyDto;
	}
	
	/**
     * 『訂正：承認申請者』リンク押下後
     * 訂正idからデータ（ユーザーId、申請者、対象日付、申請日）を取得
     * @param id 訂正id
     * @return correctionDto 訂正データ
     */
	public AttendanceCorrectionDto getCorrectionDataById(Integer id) {
		AttendanceCorrectionDto correctionDto = attendanceCorrectionMapper.findCorrectionDataById(id);
		// 日付のフォーマット処理（String型yyyy/MM/dd）
		String formattedDate = customDateUtil.dateHyphenSlashConverter(correctionDto.getDate());
		String formattedApplicationDate = customDateUtil.dateHyphenSlashConverter(correctionDto.getApplicationDate());
		// フォーマット済みの値をDtoにセット
		correctionDto.setFormattedDate(formattedDate); // String型（yyyy/MM/dd）
		correctionDto.setFormattedApplicationDate(formattedApplicationDate); // String型（yyyy/MM/dd）
		
		return correctionDto;
	}

	/**
	 * 月次申請承認 statusを2に更新
	 * @param userId
	 * @param targetYearMonthAtDay
	 * @return true:承認成功 false:失敗
	 */
	public boolean updateStatusApprove(Integer userId, LocalDate targetYearMonthAtDay) {
		return monthlyAttendanceReqMapper.updateApproveStatus(userId, targetYearMonthAtDay);
	}

	/**
	 * 月次申請却下 却下理由、statusを3に更新
	 * @param userId
	 * @param targetYearMonthAtDay
	 * @param comment
	 * @return
	 */
	public boolean updateStatusReject(Integer userId, LocalDate targetYearMonthAtDay, String comment) {
		return monthlyAttendanceReqMapper.updateRejectStatusAndComment(userId, targetYearMonthAtDay, comment);
	}
	
	/**
	 * 訂正申請承認 勤怠情報を更新
	 * @param id
	 * @param confirmer
	 * @return true:承認成功 false:失敗
	 */
	public boolean updateApproveCorrection(Integer id, String confirmer) {
		boolean isAttendanceUpdate = attendanceMapper.updateAttendanceFromCorrection(id) > 0;
		boolean isCorrectionUpdate = attendanceCorrectionMapper.updateApproveCorrection(confirmer, id) > 0;
		return isAttendanceUpdate && isCorrectionUpdate;
	}
	
	/**
	 * 訂正申請却下 却下理由、削除フラグ、確認者更新
	 * @param confirmer
	 * @param rejectionReason
	 * @param id
	 * @return true:却下成功 false:失敗
	 */
	public boolean updateRejectCorrection(AttendanceCorrectionForm correctionForm) {
		return attendanceCorrectionMapper.updateRejectCorrection(correctionForm);
	}
	
	/**
	 * 月次申請者情報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return 月次申請者の勤怠リスト
	 */
	public List<Attendance> findByUserIdAndYearMonth(Integer userId, YearMonth targetYearMonth) {
		return attendanceMapper.findAllDailyAttendance(userId, targetYearMonth);
	}
	
	/**
	 * 対象の訂正申請情報取得
	 * @param userId
	 * @param date 対象の日付（年月日）
	 * @return 訂正申請者の勤怠情報
	 */
	public AttendanceCorrection findCorrectionByUserIdAndDate(Integer userId, LocalDate date){
		return attendanceCorrectionMapper.findAttendanceByUserIdAndDate(userId, date);
	}
	
	
	/**
	 * 対象の却下された訂正申請リスト取得
	 * @param userId
	 * @param targetYearMonth 対象の年月
	 * @return 訂正申請者の却下勤怠リスト
	 */
	public List<AttendanceCorrection> findRejectedByUserIdAndYearMonth(Integer userId, YearMonth targetYearMonth){
		return attendanceCorrectionMapper.findRejectedAttendanceByUserIdAndYearMonth(userId, targetYearMonth);
	}
	
	/**
	 * 対象の申請中の訂正申請リスト取得
	 */
	public List<AttendanceCorrection> findRequestedByUserIdAndYearMonth(Integer userId, YearMonth targetYearMonth){
		return attendanceCorrectionMapper.findReqestedCorrectionByUserIdAndYearMonth(userId, targetYearMonth);
	}
	
	
//	/**
//	 * 対象のユーザーidとページ番号に基づいて訂正情報を取得
//	 * @param userId
//	 * @param page ページ番号(0から始まる)
//	 * @param pageSize 1ページ当たりの項目数
//	 * @return 訂正情報のリスト
//	 */
//	public List<AttendanceCorrection> getCorrectionsByUserId(Integer userId, Integer page, Integer pageSize){
//		// オフセットを計算(ページ番号 * 1ページ当たりの件数)
//		Integer offset = page * pageSize;
//		return attendanceCorrectionMapper.findCorrections(userId, offset, pageSize);
//	}
//	
//	/**
//	 * 対象のユーザーIDの訂正申請の総件数を取得
//	 * @param userId
//	 * @return 訂正申請の総件数
//	 */
//	public Integer countCorrectionsByUserId(Integer userId) {
//		return attendanceCorrectionMapper.countCorrections(userId);
//	}
	
	
	/**
	 * 却下理由【訂正申請】×ボタン押下(却下フラグ0に更新)
	 * @param correctionId
	 */
	public void updateRejectFlg(Integer correctionId) {
		attendanceCorrectionMapper.updateRejectFlg(correctionId);
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
			String startTime = dailyAttendanceForm.getStartTime();
			String endTime = dailyAttendanceForm.getEndTime();
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
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime",
									"休日に出勤時間は入力できません"));
				}
				if (endTime != "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime",
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
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime",
									"出勤時間を入力して下さい"));
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime",
									"退勤時間を入力して下さい"));
					i++;
					continue;
				}
				//出勤時間または退勤時間のどちらかが空白の場合
				if (endTime == "" && startTime != "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime",
									"退勤時間を入力して下さい"));
				}

				if (startTime == "" && endTime != "") {
					result.addError(
							new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime",
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
								new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime",
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
								new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime",
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
									new FieldError("attendanceFormList", "attendanceFormList[" + i + "].startTime",
											"出勤時間は退勤時間より先になるように入力して下さい"));
							result.addError(
									new FieldError("attendanceFormList", "attendanceFormList[" + i + "].endTime",
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

			Byte status = dailyAttendanceForm.getStatus();
			Integer registedUserId = dailyAttendanceForm.getUserId();
			LocalDate date = dailyAttendanceForm.getDate();
			String endTime = dailyAttendanceForm.getEndTime();
			String startTime = dailyAttendanceForm.getStartTime();
			LocalTime convertedStartTime = null;
			LocalTime convertedEndTime = null;

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
						convertedStartTime = LocalTime.parse(startTime);
						//						dailyAttendanceForm.setStartTime(convertedStartTime);
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

						convertedEndTime = LocalTime.parse(endTime);
						//						dailyAttendanceForm.setEndTime(convertedEndTime);
					} catch (IllegalArgumentException e) {
						// 無効なフォーマットの場合のエラーハンドリング

					}
				}
			}

			Attendance registAttendance = new Attendance();

			registAttendance.setUserId(userId);
			registAttendance.setDate(date);
			registAttendance.setStatus(status);
			registAttendance.setRemarks(dailyAttendanceForm.getRemarks());
			registAttendance.setStartTime(convertedStartTime);
			registAttendance.setEndTime(convertedEndTime);

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
	/**
	 * 本日出勤打刻済みか確認
	 * @param userId
	 * @param today
	 * @return 本日出勤打刻しているかの真偽
	 */
	public boolean findTodaysStartTime(Integer userId, LocalDate today) {
		return attendanceMapper.todaysStartTimeExistCheck(userId,today);
	}

	/**
	 * 出勤登録
	 * @param userId
	 * @param today
	 * @param now
	 * @return 反映結果
	 * 
	 */
	public boolean insertStartTime(Integer userId, LocalDate today, LocalTime now) {
		//勤怠ステータスを判別する
		Byte status = determineStatus(userId, today, now);
		Attendance attendance = new Attendance();
		attendance.setUserId(userId);
		attendance.setDate(today);
		attendance.setStatus(status);
		attendance.setStartTime(now);

		boolean isRegistStartTime = false;
		//インサートできなかったら自動でアップデートに切り替えるマッパーを利用
		isRegistStartTime = attendanceMapper.upsertStartTime(attendance);
		return isRegistStartTime;

	}

	/**
	 * 退勤登録
	 * @param userId
	 * @param today
	 * @param now
	 * @return 反映結果
	 */
	public boolean updateEndTime(Integer userId, LocalDate today, LocalTime now) {
		//勤怠ステータスを判別する
		Byte status = determineStatus(userId, today, now);
		Attendance attendance = new Attendance();
		attendance.setUserId(userId);
		attendance.setDate(today);
		attendance.setStatus(status);
		attendance.setEndTime(now);

		boolean isRegistEndTime = false;
		//退勤時間更新
		isRegistEndTime = attendanceMapper.updateEndTime(attendance);
		return isRegistEndTime;

	}

	/**
	 * 勤怠ステータス判別(実装予定)	
	 * @param userId
	 * @param today
	 * @param now
	 * @return
	 */
	Byte determineStatus(Integer userId, LocalDate today, LocalTime now) {
		Byte status = null;
		//今後ユーザー毎に定時を設定できるようにユーザーIDを引数にしている。

		status = 0;

		return status;
	}

	// 訂正入力チェック
	public void correctionValidationForm(AttendanceCorrectionForm correctionForm, BindingResult result) {
		System.out.println("サービスクラスの入力チェック");
		
		String startTime = correctionForm.getStartTime();
		String endTime = correctionForm.getEndTime();
		String remarks = correctionForm.getRemarks();
		Byte status = correctionForm.getStatus();
		List<Byte> attendanceSystem = AttendanceUtil.getAttendanceSystem();
		List<Byte> holidaySystem = AttendanceUtil.getHolidaySystem();
		
		System.out.println("フォームの中身" + correctionForm);
		System.out.println("開始時間: " + startTime);
		
		// startTimeのチェック
	    if (startTime == null) {
	        result.addError(new FieldError("correctionForm", "startTime", "開始時間は必須です。"));
	        System.out.println(result.getFieldError().getDefaultMessage());
	    } else {
	        // startTimeが適切なフォーマットかチェック（例: HH:mm）
	        if (!startTime.matches("\\d{2}:\\d{2}")) {
	            result.addError(new FieldError("correctionForm", "startTime", "開始時間はHH:mm形式で入力してください。"));
	            System.out.println(result.getFieldError().getDefaultMessage());
	        }
	    }

	    // endTimeのチェック
	    if (endTime == null) {
	        result.addError(new FieldError("correctionForm", "endTime", "終了時間は必須です。"));
	        System.out.println(result.getFieldError().getDefaultMessage());
	    } else {
	        // endTimeが適切なフォーマットかチェック（例: HH:mm）
	        if (!endTime.matches("\\d{2}:\\d{2}")) {
	            result.addError(new FieldError("correctionForm", "endTime", "終了時間はHH:mm形式で入力してください。"));
	            System.out.println(result.getFieldError().getDefaultMessage());
	        }
	    }
		
		//備考欄
	    System.out.println("備考欄の内容: " + remarks);

		if (remarks != null && !remarks.isEmpty()) {
			System.err.println("備考欄チェック");
		    System.out.println("備考欄の内容: " + remarks);
		    
		    String regex = "[^\\x00-\\x7F]";
		    Pattern pattern = Pattern.compile(regex);
		    Matcher matcher = pattern.matcher(remarks);
		    
		    if (remarks.length() > 20) {
		        result.addError(new FieldError("correctionForm", "remarks", "20字以内で入力して下さい"));
		        FieldError fieldError = result.getFieldError("remarks");
		        if (fieldError != null) {
		            System.out.println(fieldError.getDefaultMessage());
		        }
		    }
			if (!matcher.find()) {
				result.addError(new FieldError("correctionForm", "remarks","全角で入力して下さい"));
				System.out.println(result.getFieldError().getDefaultMessage());
			}
			if (status == null) {
				result.addError(new FieldError("correctionForm", "status","勤怠状況を入力して下さい"));
				System.out.println(result.getFieldError().getDefaultMessage());
			}
		}
//		//休日系統
//		if (holidaySystem.contains(status)) {
//			if (!startTime.isEmpty()) {
//				result.addError(new FieldError("correctionForm", "startTime","休日に出勤時間は入力できません"));
//			}
//			if (!endTime.isEmpty()) {
//				result.addError(new FieldError("correctionForm", "endTime","休日に退勤時間は入力できません"));
//			}
//		}
//
//		//出勤系統
//		if (attendanceSystem.contains(status)) {
//			//出勤時間も退勤時間も入力していない場合(以降の入力チェックが不要のためブレイク)
//			if (startTime.isEmpty() && endTime.isEmpty()) {
//				System.out.println("出勤系統で時間null");
//				result.addError(new FieldError("correctionForm", "startTime", "出勤時間を入力して下さい"));
//				result.addError(new FieldError("correctionForm", "endTime", "退勤時間を入力して下さい"));
//			}
//			//出勤時間または退勤時間のどちらかが空白の場合
//			if (endTime == "" && startTime != "") {
//				result.addError(new FieldError("correctionForm", "endTime", "退勤時間を入力して下さい"));
//			}
//
//			if (startTime == "" && endTime != "") {
//				result.addError(new FieldError("correctionForm", "startTime", "出勤時間を入力して下さい"));
//			}
//		}
//		//桁数補足
//		if (startTime != "" || endTime != "") {
//
//			if (startTime.matches("\\d{1}:\\d{2}")) {
//				startTime = "0" + startTime;
//			}
//			if (endTime.matches("\\d{1}:\\d{2}")) {
//				endTime = "0" + endTime;
//			}
//
//			//DateTimeParseException eで同じことやっているのでコメントアウト
//			//出勤時刻の形式が不正な場合
//			if (startTime != "") {
//				// HH:mm形式の時刻を検出する正規表現
//				String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
//
//				// パターンをコンパイル
//				Pattern pattern = Pattern.compile(regex);
//				Matcher matcher = pattern.matcher(startTime);
//				if (!matcher.find()) {
//					result.addError(new FieldError("correctionForm", "startTime", "HH:mm形式で入力して下さい"));
//				}
//			}
//			//退勤時刻の形式が不正な場合
//			if (endTime != "") {
//				String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
//
//				// パターンをコンパイル
//				Pattern pattern = Pattern.compile(regex);
//				Matcher matcher = pattern.matcher(endTime);
//				if (!matcher.find()) {
//					result.addError(new FieldError("correctionForm", "endTime","HH:mm形式で入力して下さい"));
//
//				}
//			}
//
//			LocalTime startInputTime;
//			LocalTime endInputTime;
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//
//			//出勤時間＞退勤時間の場合
//
//			try {
//				startInputTime = LocalTime.parse(startTime, formatter);
//
//				try {
//					endInputTime = LocalTime.parse(endTime, formatter);
//
//					if (startInputTime.isAfter(endInputTime)) {
//						result.addError(
//								new FieldError("correctionForm", "startTime", "出勤時間は退勤時間より先になるように入力して下さい"));
//						result.addError(
//								new FieldError("correctionForm", "endTime", "退勤時間は出勤時間より後になるように入力して下さい"));
//					}
//
//				} catch (DateTimeParseException e) {
//
//				}
//
//			} catch (DateTimeParseException e) {
//
//			}
//
//		}
//		if ((startTime != "" && endTime != "") && status == null) {
//			result.addError(new FieldError("correctionForm", "status", "勤怠状況を入力して下さい"));
//
//		}
	}
	
	/**
	 * 勤怠訂正モーダルの申請ボタン押下後
	 * @param correctionForm
	 */
	public String registCorrection(AttendanceCorrectionForm correctionForm) {
		AttendanceCorrection correction = new AttendanceCorrection();
		correction.setUserId(correctionForm.getUserId());
		correction.setStatus(correctionForm.getStatus());

		// 日付 2024/01/01から2024-01-01へ変換してdateを設定
		System.out.println("コレクションフォーム" + correctionForm);
		System.out.println("ゲットdate" + correctionForm.getDate());
		String formattedDate = correctionForm.getDate().replace("/", "-");
		correction.setDate(LocalDate.parse(formattedDate));
		
		// 出勤時間 nullじゃなかったらparseする,nullだったらそのまま
		if (correctionForm.getStartTime() != null && !correctionForm.getStartTime().isEmpty()) {
		    correction.setStartTime(LocalTime.parse(correctionForm.getStartTime()));
		} else {
		    correction.setStartTime(null);
		}

		// 退勤時間
		if (correctionForm.getEndTime() != null && !correctionForm.getEndTime().isEmpty()) {
		    correction.setEndTime(LocalTime.parse(correctionForm.getEndTime()));
		} else {
		    correction.setEndTime(null);
		}

	    correction.setCorrectionReason(correctionForm.getCorrectionReason());
	    correction.setRejectionReason(correctionForm.getRejectionReason());
		correction.setRemarks(correctionForm.getRemarks());
		correction.setApplicationDate(LocalDate.now());
		correction.setRejectFlg((byte) 0);

		attendanceCorrectionMapper.registAttendanceCorrection(correction);

		return correctionForm.getDate().replace("-", "/") + "の訂正申請が完了しました。";
	}


	

}
