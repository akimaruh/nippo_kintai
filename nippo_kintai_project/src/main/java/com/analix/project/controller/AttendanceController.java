package com.analix.project.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

	/**
	 * 初期表示
	 * @param userId
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		// ヘッダー:ユーザ名、ユーザーID
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);

		// 初期表示で「登録ボタン」を非活性にさせるためのフラグ
		boolean displayFlg = true;
		model.addAttribute("displayFlg", displayFlg);

		return "/attendance/regist";
	}

	/**
	 * 『表示』ボタン押下後
	 * @param yearMonth
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist/display", method = RequestMethod.GET)
	public String attendanceDisplay(@RequestParam("yearMonth") String yearMonth,
			Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		
		
		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Date attendanceDate = java.sql.Date.valueOf(yearMonth + "-01");
		session.setAttribute("attendanceDate", attendanceDate);
		Integer status = attendanceService.findStatusByUserId(user.getId(), attendanceDate);

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

		model.addAttribute("statusFlg", statusFlg);
		model.addAttribute("status", status);
		session.setAttribute("yearMonth", yearMonth);

		Users loginUser = (Users) session.getAttribute("loginUser");
		Integer userId = loginUser.getId();

		attendanceFormList.setAttendanceFormList(attendanceService.getFindAllDailyAttendance(userId, yearMonth));
		if(attendanceFormList.getAttendanceFormList().contains(null)) {
			model.addAttribute("error", "年月を入力してください");
			
			return "/attendance/regist";
		}

		//１か月分登録されると活性化
		boolean monthlyRegistCheck = attendanceService.applicableCheck(attendanceFormList.getAttendanceFormList());

		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("registCheck", monthlyRegistCheck);

		return "/attendance/regist";
	}

	/**
	 * 『登録』ボタン押下後
	 * @param userId
	 * @param attendanceFormList
	 * @param result
	 * @param model
	 * @param session
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist/complete", method = RequestMethod.POST)
	public String attendanceComplete(@RequestParam("sessionUserId") Integer userId,
			@Validated @ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) {

		attendanceService.validationForm(attendanceFormList, result);
		String yearMonth = (String) session.getAttribute("yearMonth");
		

		if (result.hasErrors()) {
			model.addAttribute("attendanceFormList", attendanceFormList);
			model.addAttribute("yearMonth", yearMonth);
			model.addAttribute("error", "エラー内容に従って修正してください");

			return "/attendance/regist";
		}

		String message = attendanceService.getRegistDailyAttendance(userId, attendanceFormList);
		
		redirectAttributes.addAttribute("yearMonth", yearMonth);
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/attendance/regist/display";

	}

	/**
	 * 『承認申請』ボタン押下後
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(path = "/attendance/approveRequestComplete", method = RequestMethod.POST)
	public String approveRequest(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();

		Date approveYearMonth = (Date) session.getAttribute("attendanceDate");

		String message = attendanceService.insertMonthlyAttendanceReq(userId, approveYearMonth);
		String yearMonth = (String) session.getAttribute("yearMonth");

		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("yearMonth", yearMonth);

		return "redirect:/attendance/regist/display";
	}

	/**
	 * 『承認申請者』リンク押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@PostMapping("/attendance/approveRequests")
	public String showApproveRequests(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") String targetYearMonth, 
			@RequestParam("name") String name, @RequestParam("date") String date,
			Model model, HttpSession session,
			AttendanceFormList attendanceFormList) {

		session.setAttribute("targetYearMonth", targetYearMonth);

		String yearMonth = targetYearMonth.substring(0, 7); // String型に変換　/2024-01/-01

		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, yearMonth);

		model.addAttribute("attendanceList", attendanceList);
		model.addAttribute("targetYearMonth", targetYearMonth);
		
		model.addAttribute("name", name);
		model.addAttribute("yearMonth", yearMonth);
		model.addAttribute("date", date);

		return "/attendance/regist";
	}

	/**
	 * 『承認』『却下』ボタン押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param action
	 * @param redirectAttributes
	 * @return
	 */
	@GetMapping("/attendance/update")
	public String updateStatus(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") String targetYearMonth, @RequestParam("action") String action,
			RedirectAttributes redirectAttributes) {

		if ("approve".equals(action)) {
			attendanceService.updateStatusApprove(userId, targetYearMonth);
			String message = attendanceService.updateStatusApprove(userId, targetYearMonth);
			redirectAttributes.addFlashAttribute("message", message);
		} else if ("reject".equals(action)) {
			attendanceService.updateStatusReject(userId, targetYearMonth);
			String message = attendanceService.updateStatusReject(userId, targetYearMonth);
			redirectAttributes.addFlashAttribute("message", message);
		}

		return "redirect:/attendance/regist";
	}
}
