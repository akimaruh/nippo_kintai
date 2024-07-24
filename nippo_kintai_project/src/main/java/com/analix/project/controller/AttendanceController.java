package com.analix.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Users;
import com.analix.project.form.DailyAttendanceForm;
import com.analix.project.service.AttendanceService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AttendanceController {

	@Autowired
	public AttendanceService attendanceService;
	
	
	//	@Autowired
	//	private Attendance attendance;

	/**
	 * 初期表示
	 * @param userId
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model,HttpSession session) {
		
		
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);
		
//		Integer userId = (Integer) session.getAttribute("id");
		
		
//		// 現在の年と月を取得
//        Calendar cal = Calendar.getInstance();
//        int currentYear = cal.get(Calendar.YEAR);
//        int currentMonth = cal.get(Calendar.MONTH) + 1;
//
//        // yearMonth文字列を作成
//        String yearMonth = String.format("%d-%02d", currentYear, currentMonth);
//		
//
//		List<LocalDate> dateList = attendanceService.generateMonthDates(yearMonth);
//		List<DailyAttendanceForm> dailyAttendanceList = new ArrayList<DailyAttendanceForm>();
//
//		for (LocalDate date : dateList) {
//
//			DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();
//			dailyAttendance.setUserId(userId);
//			dailyAttendance.setDate(date);
//			dailyAttendanceList.add(dailyAttendance);
//
//		}
//
//		model.addAttribute("dailyAttendanceList", dailyAttendanceList);
//		System.out.println(dailyAttendanceList);
		

		return "/attendance/regist";
	}

	/*
	 * 『表示』ボタン押下後
	 */
	@RequestMapping(path = "/attendance/display", method = RequestMethod.POST)
	public String attendanceDisplay(@RequestParam("yearMonth") String yearMonth,
		Model model,HttpSession session) {
		System.out.println("コントローラクラス"+yearMonth);
		Users loginUser = (Users)session.getAttribute("loginUser");
		Integer userId = loginUser.getId();
		System.out.println(loginUser.getId());
    
//		List<LocalDate> dateList = attendanceService.generateMonthDates(yearMonth);
		List<DailyAttendanceForm> dailyAttendanceList = attendanceService.getFindAllDailyAttendance(userId,yearMonth);
		
//		for (LocalDate date : dateList) {
//			if()
//			DailyAttendanceForm dailyAttendance = new DailyAttendanceForm();
//			dailyAttendance.setUserId(userId);
//			dailyAttendance.setDate(date);
//			dailyAttendanceList.add(dailyAttendance);
//
//		}
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("dailyAttendanceList", dailyAttendanceList);

		


		return "/attendance/regist";

	}

}
