package com.analix.project.service;

import java.sql.Date;
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
	 * 一覧表示
	 * @param userId
	 * @param year
	 * @param month
	 * @return 勤怠一覧取得マッパー
	 */
	public List<DailyAttendanceForm> getFindAllDailyAttendance(Integer userId, String yearMonth) {
		System.out.println(userId);
		 List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<>();
		    List<Attendance> attendanceListSearchForUserIdAndYearMonth = attendanceMapper.findAllDailyAttendance(userId, yearMonth);
		    System.out.println(attendanceListSearchForUserIdAndYearMonth); 
		    List<LocalDate> dateList = generateMonthDates(yearMonth);

		    // Attendance情報をLocalDateでインデックス化するMapを作成
		    Map<LocalDate, Attendance> attendanceMap = attendanceListSearchForUserIdAndYearMonth.stream()
		            .collect(Collectors.toMap(
		                    attendance -> customDateUtil.convertToLocalDate(attendance.getDate()),
		                    attendance -> attendance
		            ));

		    for (LocalDate date : dateList) {
		        DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();
		        dailyAttendance.setId(userId);
		        dailyAttendance.setDate(date);

		        // 該当する日付の出席情報を取得
		        Attendance attendance = attendanceMap.get(date);

		        if (attendance != null) {
		            dailyAttendance.setStatus(attendance.getStatus());
		            dailyAttendance.setStartTime(attendance.getStartTime());
		            dailyAttendance.setEndTime(attendance.getEndTime());
		            dailyAttendance.setRemarks(attendance.getRemarks());
		        }

		        dailyAttendanceList.add(dailyAttendance);
		    }

		    return dailyAttendanceList;

	}

	/**
	 * 承認申請取得
	 * @return
	 */
	public List<MonthlyAttendanceReqDto> getMonthlyAttendanceReq() {
		return monthlyAttendanceReqMapper.findAll();
	}
	
	/**
	 * status更新 承認・却下
	 */
		public void updateStatusApprove(Integer id) {
			monthlyAttendanceReqMapper.updateStatusApprove(id);
		}
 
		public void updateStatusReject(Integer id) {
			monthlyAttendanceReqMapper.updateStatusReject(id);
		}
		

	/**
	 * 勤怠登録
	 * @param dailyAttendanceForm
	 * @return 勤怠登録マッパー
	 */
	public Boolean getRegistDailyAttendance(DailyAttendanceForm dailyAttendanceForm) {
		Attendance registAttendance = new Attendance();
		LocalDate date = dailyAttendanceForm.getDate();
		Date AttendanceDate = customDateUtil.convertToSqlDate(date);

		registAttendance.setId(dailyAttendanceForm.getId());
		registAttendance.setUserId(dailyAttendanceForm.getUserId());
		registAttendance.setDate(AttendanceDate);
		registAttendance.setStatus(dailyAttendanceForm.getStatus());
		registAttendance.setRemarks(dailyAttendanceForm.getRemarks());
		registAttendance.setStartTime(dailyAttendanceForm.getStartTime());
		registAttendance.setEndTime(dailyAttendanceForm.getEndTime());
		return attendanceMapper.registDailyAttendance(registAttendance);

	}

	/***
	 * 勤怠修正
	 * @param dailyAttendanceForm
	 * @return 勤怠修正マッパー
	 */
	public Boolean getUpdateDailyAttendance(DailyAttendanceForm dailyAttendanceForm) {
		Attendance updateAttendance = new Attendance();
		LocalDate date = dailyAttendanceForm.getDate();
		Date AttendanceDate = customDateUtil.convertToSqlDate(date);

		updateAttendance.setId(dailyAttendanceForm.getId());
		updateAttendance.setUserId(dailyAttendanceForm.getUserId());
		updateAttendance.setDate(AttendanceDate);
		updateAttendance.setStatus(dailyAttendanceForm.getStatus());
		updateAttendance.setRemarks(dailyAttendanceForm.getRemarks());
		updateAttendance.setStartTime(dailyAttendanceForm.getStartTime());
		updateAttendance.setEndTime(dailyAttendanceForm.getEndTime());
		return attendanceMapper.registDailyAttendance(updateAttendance);
	}

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
	public  List<LocalDate> generateMonthDates(String yearMonth) {
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
	        	System.err.println("Invalid date string: " +yearMonth);
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
