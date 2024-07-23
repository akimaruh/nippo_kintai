package com.analix.project.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.service.AttendanceService;

@Controller
public class AttendanceController {

	@Autowired
	public AttendanceService attendanceService;

	/**
	 * 初期表示
	 * @param userId
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model) {

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		int currentMonth = cal.get(Calendar.MONTH);

		List<LocalDate> dateList = attendanceService.generateMonthDates(currentYear, currentMonth);
		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();

		for (LocalDate date : dateList) {

			DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();
			dailyAttendance.setDate(date);
			dailyAttendanceList.add(dailyAttendance);

		}

		model.addAttribute("DailyAttendanceList", dailyAttendanceList);
		System.out.println(dailyAttendanceList);
		

		return "/attendance/regist";
	}

	/*
	 * 『表示』ボタン押下後
	 */
	@RequestMapping(path = "/attendance/display", method = RequestMethod.GET)
	public String attendanceDisplay(@RequestParam(required = false) Integer userId, @RequestParam("year") int year,
			@RequestParam("month") int month, Model model) {

		List<LocalDate> dateList = attendanceService.generateMonthDates(year, month);
		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();
		for (LocalDate date : dateList) {
			DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();
			dailyAttendance.setDate(date);
			dailyAttendanceList.add(dailyAttendance);

		}

		model.addAttribute("DailyAttendanceList", dailyAttendanceList);

		return "/attendance/regist";

	}

}
