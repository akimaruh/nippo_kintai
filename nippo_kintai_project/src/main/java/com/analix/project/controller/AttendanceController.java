package com.analix.project.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.service.AttendanceService;

import jakarta.servlet.http.HttpServletRequest;
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
	public String attendanceRegist(Model model, HttpSession session,AttendanceFormList attendanceFormList) {
		// ヘッダー:ユーザ名、ユーザーID
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		String statusFlg = attendanceService.findStatusByUserId(user.getId());

		model.addAttribute("statusFlg", statusFlg);
	
	

		//				Integer userId = (Integer) session.getAttribute("id");

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
	@RequestMapping(path = "/attendance/regist/display", method = RequestMethod.POST)
	public String attendanceDisplay(@RequestParam("yearMonth") String yearMonth,
			Model model, HttpSession session,AttendanceFormList attendanceFormList) {
		
		// ヘッダーのステータス部分
		Integer status = (Integer) session.getAttribute("status");
		Users user = (Users) session.getAttribute("loginUser");
		String statusFlg = attendanceService.findStatusByUserId(user.getId());

		model.addAttribute("statusFlg", statusFlg);
		
		session.setAttribute("yearMonth", yearMonth);
		

		System.out.println("コントローラクラス" + yearMonth);
		Users loginUser = (Users) session.getAttribute("loginUser");
		Integer userId = loginUser.getId();
		System.out.println(loginUser.getId());
		

		//		List<LocalDate> dateList = attendanceService.generateMonthDates(yearMonth);
		//		AttendanceFormList attendanceFormList = attendanceService.getFindAllDailyAttendance(userId, yearMonth);
		 attendanceFormList.setAttendanceFormList(attendanceService.getFindAllDailyAttendance(userId, yearMonth));
		 attendanceFormList.getAttendanceFormList().get(0).getDate();
		//		boolean attendanceNotEnterdFlg = attendanceFormList;
		System.out.println("表示ボタン押下後" + attendanceFormList);
		
		//１か月分登録されると活性化
		attendanceService.applicableCheck(userId, yearMonth);
		
		

		model.addAttribute("yearMonth", yearMonth);
//		model.addAttribute("attendanceFormList", attendanceFormList);
	

		return "/attendance/regist";
	}

	/*
	 * 『登録』ボタン押下後
	 */
	@RequestMapping(path = "/attendance/regist/complete", method = RequestMethod.POST)
	public String attendanceComplete(HttpServletRequest request, @RequestParam("sessionUserId") Integer userId,
			@Validated @ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model) {
		
		System.out.println("登録ボタン押下後" + attendanceFormList);
		boolean errorFlg = false;
		List<String>errorBox = new ArrayList<>();
		errorBox= attendanceService.validationForm(attendanceFormList, result);
		System.out.println();
		
		if (result.hasErrors()) {
			model.addAttribute("errorFlg",errorFlg);
			model.addAttribute("errorBox",errorBox);
			model.addAttribute("attendanceFormList",attendanceFormList);
			
			return "/attendance/regist";
		}
		
		
//		for(DailyAttendanceForm completeAttendance :attendanceFormList.getAttendanceFormList()) {
//			//LocalDate型で受け取り
//			LocalDate newDateString = completeAttendance.getDate();
//			System.out.println(newDateString);
//			//String型で受け取り
//			String newStartTimeString = completeAttendance.getStartTime2();
//			System.out.println(newStartTimeString);
//			String newEndTimeString = completeAttendance.getEndTime2();
			
			
			
//			LocalDate newDateS = LocalDate.parse(newDateString);			
//			LocalDate newStartTime = LocalDate.parse(newStartTimeString);
//			LocalDate  newEndTime = LocalDate.parse(newEndTimeString);
//			
//			completeAttendance.setDate(newDateS);
//			completeAttendance.setStartTimeLocalDate(newStartTime);
//			completeAttendance.setEndTimeLocalDate(newEndTime);
//			result = attendanceService.validationForm(attendanceFormList, result);
//			if (result.hasErrors()) {
//				return "redirect:/attendance/regist/display";
//			}
//		}
		
		
		
		

		

//		System.out.println(attendanceFormList);

		//			//	@RequestMapping(path = "/attendance/regist/complete", method = RequestMethod.POST)
		//			//	public String attendanceComplete(HttpServletRequest request, @RequestParam("userId") Integer userId,
		//			//			@ModelAttribute ("date") String[] date,
		//			//            @RequestParam("status") byte[] statuses,
		//			//            @RequestParam("startTime") Time startTimes,
		//			//            @RequestParam("endTime") String[] endTimes,
		//			//            @RequestParam("remarks") String[] remarks, Model model) {
		//
		//			//		System.out.println("登録ボタン押下後" + attendanceFormList.getAttendanceFormList());
		//			//	System.out.println("登録ボタン押下後"+dailyAttendanceList.get(0));
		//			// デバッグ用: リクエストメソッドの出力
		//			System.out.println("Request Method: " + request.getMethod());
		//
		//			// デバッグ用: 送信されたパラメータをすべて出力
		//			Enumeration<String> parameterNames = request.getParameterNames();
		//			while (parameterNames.hasMoreElements()) {
		//				String paramName = parameterNames.nextElement();
		//				System.out.println(paramName + ": " + request.getParameter(paramName));
		//			}

		//	    // デバッグ用: 送信されたパラメータをすべて出力
		//	    Enumeration<String> parameterNames = request.getParameterNames();
		//	    while (parameterNames.hasMoreElements()) {
		//	        String paramName = parameterNames.nextElement();
		//	        System.out.println(paramName + ": " + request.getParameter(paramName));
		//	    }
//		String newDateString = dailyAttendanceFormList .getDate2(); 
//		Date newDateS = java.sql.Date.valueOf((newDateString == "" ? null:newDateString));
		attendanceService.getRegistDailyAttendance(userId, attendanceFormList);

		//	    for (DailyAttendanceDto attendance : dailyAttendanceList) {
		//	        System.out.println("Date: " + attendance.getDate());
		//	        System.out.println("Status: " + attendance.getStatus());
		//	        System.out.println("Start Time: " + attendance.getStartTime());
		//	        System.out.println("End Time: " + attendance.getEndTime());
		//	        System.out.println("Remarks: " + attendance.getRemarks());
		//	    }
		return "redirect:/attendance/regist";

	}
	
	/*
	 * 『承認申請』ボタン押下後
	 */
	@RequestMapping(path="/attendance/approveRequestComplete", method = RequestMethod.POST)
	public String approveRequest(HttpSession session, Model model) {
		String approveYearMonth = (String)session.getAttribute("yearMonth");
	    System.out.println("Received yearMonth:" + approveYearMonth);
	    
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		
//		LocalDate attendanceDate = LocalDate.of(2024,07,01); //ここなおす
		Date attendanceDate = java.sql.Date.valueOf(approveYearMonth + "-01"); // "yyyy-MM-dd" 形式に変換
		
        System.out.println("コuserID:" + userId);
        System.out.println("コattendanceDate:" + attendanceDate);
		
        
		String message = attendanceService.insertMonthlyAttendanceReq(userId, attendanceDate);
		System.out.println(message);
	
//		model.addAttribute(isApprove);
		
		return "redirect:/attendance/regist";
	}

	/*
	 * 『承認申請者』リンク押下後
	 */
	@GetMapping("/attendance/approveRequests")
	public String showApproveRequests(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") String targetYearMonth, Model model, HttpSession session,AttendanceFormList attendanceFormList) {
		System.out.println("UserId: " + userId);
		System.out.println("Original targetYearMonth: " + targetYearMonth);

		session.setAttribute("targetYearMonth", targetYearMonth);

		String yearMonth = targetYearMonth.substring(0, 7); // String型に変換　/2024-01/-01

		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, yearMonth);

		System.out.println(attendanceList.get(0).getDate());
		System.out.println("Attendance List Size: " + attendanceList.size());
		for (Attendance a : attendanceList) {
			System.out.println(a);
		}

		model.addAttribute("attendanceList", attendanceList);

		return "/attendance/regist";

	}

	/*
	 * 『承認』『却下』ボタン押下後
	 */
	@GetMapping("/attendance/update")
	public String updateStatus(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") String targetYearMonth, @RequestParam("action") String action) {
		System.out.println("updateStatusUserId: " + userId);
		System.out.println("updateStatusTargetYearMonth: " + targetYearMonth);

		if ("approve".equals(action)) {
			attendanceService.updateStatusApprove(userId, targetYearMonth);
		} else if ("reject".equals(action)) {
			attendanceService.updateStatusReject(userId, targetYearMonth);
		}

		return "redirect:/attendance/regist";
	}
}
