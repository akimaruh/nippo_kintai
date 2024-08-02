package com.analix.project.service;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

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
	 * ヘッダー:ステータス部分
	 */
	public String findStatusByUserId(Integer userId) {
		Integer status = monthlyAttendanceReqMapper.findStatusByUserId(userId);
		String statusFlg;

		if (status == null) {
			statusFlg = "未申請";
		} else {
			switch (status) {
			case 1:
				statusFlg = "申請中";
				break;
			case 2:
				statusFlg = "承認済";
				break;
			case 3:
				statusFlg = "却下";
				break;
			default:
				statusFlg = "未申請";
				break;
			}
		}
		return statusFlg;
	}

	/**
	 * 一覧表示
	 * @param userId
	 * @param year
	 * @param month
	 * @return 勤怠一覧取得マッパー
	 */
	public List<DailyAttendanceForm> getFindAllDailyAttendance(Integer userId, String yearMonth) {
		//		public AttendanceFormList getFindAllDailyAttendance(Integer userId, String yearMonth) {

		System.out.println(userId);
		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();
		//		dailyAttendanceList.setAttendanceFormList(new ArrayList<DailyAttendanceForm>());
		List<Attendance> attendanceListSearchForUserIdAndYearMonth = attendanceMapper.findAllDailyAttendance(userId,
				yearMonth);
		System.out.println(attendanceListSearchForUserIdAndYearMonth);

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
			System.out.println(date);
			// 該当する日付の出席情報を取得
			Attendance attendance = attendanceMap.get(date);

			if (attendance != null) {
				dailyAttendance.setId(attendance.getId());
				dailyAttendance.setUserId(attendance.getUserId());
				dailyAttendance.setStatus(attendance.getStatus());

				//				if (endTime != null && !endTime.trim().isEmpty()) {
				//					try {
				//						// 入力が "HH:MM" 形式の場合、秒を追加して "HH:MM:SS" 形式に変換
				//						if (endTime.matches("\\d{2}:\\d{2}")) {
				//							endTime = endTime + ":00";
				//						}
				//						Time convertedEndTime = java.sql.Time.valueOf(endTime);
				//						dailyAttendanceForm.setStartTime(convertedEndTime);
				//					} catch (IllegalArgumentException e) {
				//						// 無効なフォーマットの場合のエラーハンドリング
				//						System.out.println("endTime変換失敗");
				//						e.printStackTrace();
				//					}

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
				System.out.println(date);
				System.out.println("テスト");

			}

			dailyAttendanceList.add(dailyAttendance);
		}
		System.out.println(dailyAttendanceList);

		//		AttendanceFormList attendanceFormList = new AttendanceFormList();
		//		attendanceFormList.setAttendanceFormList(dailyAttendanceList);

		//		return attendanceFormList;
		return dailyAttendanceList;
	}
	
	/**
	 *「承認申請」ボタン押下後
	 * @Param userId
	 * @Param attendanceDate
	 */
	public String insertMonthlyAttendanceReq(Integer userId, Date attendanceDate) {
        System.out.println("サuserID:" + userId);
        System.out.println("サattendanceDate:" + attendanceDate);
        
        MonthlyAttendanceReqDto monthlyDto = new MonthlyAttendanceReqDto();
        monthlyDto.setUserId(userId);
        monthlyDto.setTargetYearMonth(attendanceDate);
        monthlyDto.setDate(java.sql.Date.valueOf(LocalDate.now()));
        monthlyDto.setStatus(1);
        
        System.out.println("サdto:" + monthlyDto);
        
        monthlyAttendanceReqMapper.insertMonthlyAttendanceReq(monthlyDto);
        
        return "OK";
        
        
	}

	/**
	 * 承認申請取得
	 * @return
	 */
	public List<MonthlyAttendanceReqDto> getMonthlyAttendanceReq() {
		return monthlyAttendanceReqMapper.findAllMonthlyAttendanceReq();
	}

	/**
	 * status更新 承認・却下
	 */
	public void updateStatusApprove(Integer userId, String targetYearMonth) {
	    monthlyAttendanceReqMapper.updateStatusApprove(userId, targetYearMonth);
	}

	public void updateStatusReject(Integer userId, String targetYearMonth) {
	    monthlyAttendanceReqMapper.updateStatusReject(userId, targetYearMonth);
	}

	/**
	 * 承認申請者情報取得
	 */
	public List<Attendance> findByUserIdAndYearMonth(Integer userId, String targetYearMonth){
//		System.out.println("Service: " +  targetYearMonth);
		return attendanceMapper.findAllDailyAttendance(userId, targetYearMonth);
	}

	//入力チェック
	public BindingResult validationForm(AttendanceFormList attendanceFormList, BindingResult result) {
		System.out.println("入力チェック入り");
		for (DailyAttendanceForm dailyAttendanceForm : attendanceFormList.getAttendanceFormList()) {

			Byte status = dailyAttendanceForm.getStatus();
			LocalDate date = dailyAttendanceForm.getDate();
			String endTime = dailyAttendanceForm.getEndTime2();
			String startTime = dailyAttendanceForm.getStartTime2();

			//DateはLocalDate型で受け取っているので使わない(後で消す)
			//			String StringDate = dailyAttendanceForm.getDate2();

			Time convertedEndTime = java.sql.Time.valueOf((endTime == "" ? null : endTime));
			Time convertedStartTime = java.sql.Time.valueOf((startTime == "" ? null : startTime));

			dailyAttendanceForm.setEndTime(convertedEndTime);
			dailyAttendanceForm.setStartTime(convertedStartTime);

			if (status != null) {

			}

		}

		return result;

	}

	/**
	/**
	 * 勤怠登録
	 * @param dailyAttendanceForm
	 * @return 勤怠登録マッパー
	 */
	public Boolean getRegistDailyAttendance(Integer userId, AttendanceFormList attendanceFormList) {

		System.out.println("サービス入った");
		System.out.println(attendanceFormList);

		List<Attendance> registAttendanceList = new ArrayList<Attendance>();//バラバラに登録するならいらないかも

		for (DailyAttendanceForm dailyAttendanceForm : attendanceFormList.getAttendanceFormList()) {
			System.out.println("for文入り");
			//LocalDate型→java.sql.Date型
			//String型→java.sql.Time型に変換
			Byte status = dailyAttendanceForm.getStatus();
			Integer registedUserId = dailyAttendanceForm.getUserId();
			System.out.println(registedUserId);
			LocalDate date = dailyAttendanceForm.getDate();
			String endTime = dailyAttendanceForm.getEndTime2();
			System.out.println();
			String startTime = dailyAttendanceForm.getStartTime2();

			//DateはLocalDate型で受け取っているので使わない(後で消す)
			//			String StringDate = dailyAttendanceForm.getDate2();
			Date convertedDate = java.sql.Date.valueOf(date);
			System.out.println("Date完了");

			System.out.println("endTime変換これから");
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
						System.out.println("startTime変換失敗");

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
						System.out.println("endTime変換失敗");
					}
				}
			}

			//			Time convertedEndTime = java.sql.Time.valueOf(endTime);

			//			if (startTime != null) {
			//
			//				Time convertedStartTime = java.sql.Time.valueOf(startTime);
			//				dailyAttendanceForm.setStartTime(convertedStartTime);
			//
			//			}

			Attendance registAttendance = new Attendance();

			registAttendance.setUserId(userId);
			registAttendance.setDate(convertedDate);
			registAttendance.setStatus(status);
			registAttendance.setRemarks(dailyAttendanceForm.getRemarks());
			registAttendance.setStartTime(dailyAttendanceForm.getStartTime());
			registAttendance.setEndTime(dailyAttendanceForm.getEndTime());
			//これいらないかも
			System.out.println("リスト格納完了");

			if (status != null) {
				if (registedUserId == null) {
					System.out.println("登録");
					System.out.println(registAttendance);
					registAttendanceList.add(registAttendance);
					attendanceMapper.registDailyAttendance(registAttendance);

				} else {
					System.out.println("更新");
					registAttendance.setId(dailyAttendanceForm.getId());
					registAttendanceList.add(registAttendance);
					System.out.println(registAttendance);
					attendanceMapper.updateDailyAttendance(registAttendance);

				}
			}

			//		for (Attendance registAttendance : registAttendanceList) {
			//			System.out.println("登録マッパー入る");
			//			attendanceMapper.updateDailyAttendance(registAttendance);
			//	
		}
		return true; //messageを出力するので保留

	}
	//入力済みの勤怠を非活性にする処理
	//	public boolean attenanceNotEnteredCheck(AttendanceFormList attedanceFormList) {
	//		
	//		for(DailyAttendanceForm dailyAttendance: attedanceFormList.getAttendanceFormList()) {
	//			boolean attendanceNotEnteredCheck = false;
	//			Integer dailyAttendanceUserId = dailyAttendance.getUserId();
	//			if(dailyAttendanceUserId ==null) {
	//				attendanceNotEnteredCheck = true;
	//				
	//			
	//			}

	//		}
	//		
	//	}

	//	/***
	//	 * 勤怠修正
	//	 * @param dailyAttendanceForm
	//	 * @return 勤怠修正マッパー
	//	 */
	//	public Boolean getUpdateDailyAttendance(DailyAttendanceForm dailyAttendanceForm) {
	//		Attendance updateAttendance = new Attendance();
	//		LocalDate date = dailyAttendanceForm.getDate();
	//		Date AttendanceDate = customDateUtil.convertToSqlDate(date);
	//
	//		updateAttendance.setId(dailyAttendanceForm.getId());
	//		updateAttendance.setUserId(dailyAttendanceForm.getUserId());
	//		updateAttendance.setDate(AttendanceDate);
	//		updateAttendance.setStatus(dailyAttendanceForm.getStatus());
	//		updateAttendance.setRemarks(dailyAttendanceForm.getRemarks());
	//		updateAttendance.setStartTime(dailyAttendanceForm.getStartTime());
	//		updateAttendance.setEndTime(dailyAttendanceForm.getEndTime());
	//		return attendanceMapper.registDailyAttendance(updateAttendance);
	//	}

	//	/**
	//	 * LocalDate型からjava.util.Date型への変換
	//	 * @param convertedDate
	//	 * @return java.util.Date型へ型変換されたconvertedDate
	//	 */
	//	public Date convertToUtilDate(LocalDate convertedDate) {
	//		return Date.from(convertedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	//	}
	/*
	 * 表示日付生成
	 */
	public List<LocalDate> generateMonthDates(String yearMonth) {
		// DateTimeFormatterを使用して年と月を解析
		//		  try {
		//	            LocalDate startDate = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		//	            System.out.println("Start date: " + startDate);
		//	        } catch (DateTimeParseException e) {
		//	            e.printStackTrace();
		//	        }
		//	    }
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start;
		try {
			System.out.println(LocalDate.parse(yearMonth + "-01", formatter));
			// yearMonthに "-01" を追加して月の最初の日を表現
			start = LocalDate.parse(yearMonth + "-01", formatter);

		} catch (DateTimeParseException e) {
			System.err.println("Invalid date string: " + yearMonth);
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
