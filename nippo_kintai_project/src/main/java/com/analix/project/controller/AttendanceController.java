package com.analix.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
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
	
	/*
	 * 『承認申請者』リンク押下後
	 */
	@GetMapping("/attedance/approveRequests")
	public String showApproveRequests(@RequestParam("userId") Integer userId, @RequestParam("targetYearMonth") String targetYearMonth, Model model) {
	    System.out.println("UserId: " + userId);
	    System.out.println("Original targetYearMonth: " + targetYearMonth);
	    
	    String yearMonth = targetYearMonth.substring(0, 7); // String型に変換　/2024-01/-01
	    
		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, yearMonth);
		
		// デバッグ用: attendanceListの内容を確認
		System.out.println("Attendance List Size: " + attendanceList.size());
	    for (Attendance a : attendanceList) {
	        System.out.println(a);
	    }
	    
		model.addAttribute("attendanceList", attendanceList);

		return "/attendance/regist";
		
	}
	
//	//	↓作成中↓
//	/*
//	 * 『承認』ボタン押下後
//	 */
//	@PostMapping("/attedance/update")
//	public String updateStatus(@RequestParam("id") Integer id) {
//		attendanceService.updateStatusApprove(id);
//		return "redirect:/attendance/regist";
//		
//	}
//	//	↑作成中↑

}
