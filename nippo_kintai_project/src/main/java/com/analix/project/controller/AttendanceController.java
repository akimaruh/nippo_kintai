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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceFormList;
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
	@RequestMapping(path = "/attendance/regist/display", method = RequestMethod.GET)
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
		

		
		attendanceFormList.setAttendanceFormList(attendanceService.getFindAllDailyAttendance(userId, yearMonth));
		 attendanceFormList.getAttendanceFormList().get(0).getDate();
		System.out.println("表示ボタン押下後" + attendanceFormList);
		
		//１か月分登録されると活性化
	 boolean monthlyRegistCheck= attendanceService.applicableCheck(attendanceFormList.getAttendanceFormList());
	 System.out.println(monthlyRegistCheck);
	 
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("registCheck", monthlyRegistCheck);
	

		return "/attendance/regist";
	}

	/*
	 * 『登録』ボタン押下後
	 */
	@RequestMapping(path = "/attendance/regist/complete", method = RequestMethod.POST)
	public String attendanceComplete(@RequestParam("sessionUserId") Integer userId,
			@Validated @ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,HttpSession session ,RedirectAttributes redirectAttributes) {
		
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

		String message = attendanceService.getRegistDailyAttendance(userId, attendanceFormList);
		String yearMonth = (String) session.getAttribute("yearMonth");
		redirectAttributes.addFlashAttribute("message",message);
		redirectAttributes.addAttribute("yearMonth",yearMonth);

	
		return "redirect:/attendance/regist/display";

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
