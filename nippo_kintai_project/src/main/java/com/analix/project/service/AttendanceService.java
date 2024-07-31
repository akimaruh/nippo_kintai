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
	 * ヘッダー部分のステータス
	 */
	public Integer findStatusByUserId(Integer userId) {
		return monthlyAttendanceReqMapper.findStatusByUserId(userId);
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

			dailyAttendance.setId(userId);
			dailyAttendance.setDate(date);
			System.out.println(date);
			// 該当する日付の出席情報を取得
			Attendance attendance = attendanceMap.get(date);

			if (attendance != null) {

				dailyAttendance.setStatus(attendance.getStatus());
				String newStartTime = new SimpleDateFormat("HH:mm").format(attendance.getStartTime());
				dailyAttendance.setStartTime2(newStartTime);

				String newEndTime = new SimpleDateFormat("HH:mm").format(attendance.getEndTime());
				dailyAttendance.setEndTime2(newEndTime);
				dailyAttendance.setRemarks(attendance.getRemarks());
				System.out.println(date);
				System.out.println("テスト");

			}

			dailyAttendanceList.add(dailyAttendance);
		}
		System.out.println(dailyAttendanceList);
		System.out.println(dailyAttendanceList.get(0).getStatus());

		//		AttendanceFormList attendanceFormList = new AttendanceFormList();
		//		attendanceFormList.setAttendanceFormList(dailyAttendanceList);

		//		return attendanceFormList;
		return dailyAttendanceList;
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
		System.out.println("Service: " +  targetYearMonth);
		return attendanceMapper.findAllDailyAttendance(userId, targetYearMonth);
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

		List<Attendance> registAttendanceList = new ArrayList<Attendance>();
		for (DailyAttendanceForm dailyAttendance : attendanceFormList.getAttendanceFormList()) {

			Attendance registAttendance = new Attendance();
			Date newDateS = java.sql.Date.valueOf(dailyAttendance.getDate2());
			Time newStartTime = Time.valueOf(dailyAttendance.getStartTime2());
			Time newEndTime = Time.valueOf(dailyAttendance.getEndTime2());
			//			Date AttendanceDate = customDateUtil.convertToSqlDate(date);

			registAttendance.setId(dailyAttendance.getId());
			registAttendance.setUserId(userId);
			registAttendance.setDate(newDateS);
			registAttendance.setStatus(dailyAttendance.getStatus());
			registAttendance.setRemarks(dailyAttendance.getRemarks());
			registAttendance.setStartTime(newStartTime);
			registAttendance.setEndTime(newEndTime);
			registAttendanceList.add(registAttendance);
			System.out.println("リスト格納完了");
		}

		for (Attendance registAttendance : registAttendanceList) {
			System.out.println("登録マッパー入る");
			attendanceMapper.updateDailyAttendance(registAttendance);
		}
		return true; //messageを出力するので保留

	}

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
