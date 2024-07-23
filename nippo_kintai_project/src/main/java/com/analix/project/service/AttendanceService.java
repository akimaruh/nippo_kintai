package com.analix.project.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Attendance;
import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.mapper.AttendanceMapper;

@Service
public class AttendanceService {
	
	@Autowired
	private AttendanceMapper attendanceMapper;

	
	/**
	 * 一覧表示
	 * @param userId
	 * @param year
	 * @param month
	 * @return 勤怠一覧取得マッパー
	 */
	public List<Attendance> getFindAllDailyAttendance(Integer userId, int year, Month month) {
		
	return attendanceMapper.findAllDailyAttendance(userId, year, month);
		
	}
	
	/**
	 * 勤怠登録
	 * @param dailyAttendanceForm
	 * @return 勤怠登録マッパー
	 */
	public Boolean getRegistDailyAttendance(DailyAttendanceForm dailyAttendanceForm){
		Attendance registAttendance = new Attendance();
		LocalDate date = dailyAttendanceForm.getDate();
		Date AttendanceDate = convertToUtilDate(date);
		
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
		Date AttendanceDate = convertToUtilDate(date);
		
		updateAttendance.setId(dailyAttendanceForm.getId());
		updateAttendance.setUserId(dailyAttendanceForm.getUserId());
		updateAttendance.setDate(AttendanceDate);
		updateAttendance.setStatus(dailyAttendanceForm.getStatus());
		updateAttendance.setRemarks(dailyAttendanceForm.getRemarks());
		updateAttendance.setStartTime(dailyAttendanceForm.getStartTime());
		updateAttendance.setEndTime(dailyAttendanceForm.getEndTime());
		return attendanceMapper.registDailyAttendance(updateAttendance);
	}
	
	/**
	 * LocalDate型からjava.util.Date型への変換
	 * @param convertedDate
	 * @return java.util.Date型へ型変換されたconvertedDate
	 */
	public Date convertToUtilDate(LocalDate convertedDate) {
		return Date.from(convertedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	/*
	 * 表示日付生成
	 */
	public List<LocalDate> generateMonthDates(int yaer,int month) {
        LocalDate start = LocalDate.of(yaer, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            dates.add(date);
        }
        return dates;
    }
}
