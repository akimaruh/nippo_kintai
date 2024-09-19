package com.analix.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.Users;
import com.analix.project.mapper.NotificationsMapper;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.InformationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class StartMenuController {
	
	@Autowired
	public AttendanceService attendanceService;
	@Autowired
	public InformationService informationService;
	@Autowired
	public NotificationsMapper notificationsMapper;

	/**
	 * 初期表示
	 * @param session
	 * @param model
	 * @return 処理メニュー画面
	 */
	@RequestMapping(path = "/common/startMenu")
	public String showMenu(HttpSession session, Model model) {

//		// メッセージ
//		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
//		List<String> managerMassage = new ArrayList<>();
//		List<String> regularMassage = new ArrayList<>();
//		
		
//		String userName = user.getName();
////
//		for (MonthlyAttendanceReqDto dto : monthlyAttendanceReqList) {
//			if (dto.getStatus() == 1) {
//				String name = dto.getName();
//				Date targetYearMonth = dto.getTargetYearMonth();
//				Date date = dto.getDate();
//				String message = "【勤怠】 " + name + "の" + targetYearMonth + "の承認申請があります。（" + date + "）";
//				managerMassage.add(message);
//
//			} else if (dto.getStatus() == 2) {
//				if (dto.getName().equals(userName)) {
//					Date targetYearMonth = dto.getTargetYearMonth();
//					LocalDate today = LocalDate.now();
//					String message = "【承認】 " + targetYearMonth + "の承認申請が承認されました。（" + today + "）";
//					regularMassage.add(message);
//				}
//			} else if (dto.getStatus() == 3) {
//				if (dto.getName().equals(userName)) {
//					Date targetYearMonth = dto.getTargetYearMonth();
//					LocalDate today = LocalDate.now();
//					String message = "【却下】 " + targetYearMonth + "の承認申請が却下されました。（" + today + "）";
//					regularMassage.add(message);
//				}
//			}
//		}
		Users user = (Users) session.getAttribute("loginUser");
		List<String> notifications = informationService.findNotification(user.getId());

		
		

		model.addAttribute("notifications", notifications);
//		model.addAttribute("regularMassage", regularMassage);

		return "common/startMenu";
	}

	/**
	 * 『ログオフ』ボタン押下後
	 * @return ページを閉じる
	 */
	@RequestMapping(path = "/common/logoff", method = RequestMethod.POST)
	public String logoff(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		redirectAttributes.addFlashAttribute("message", "ログオフしました。再度ログインしてください。");

		return "redirect:/";//JavaScriptで閉じないならこっち
	}

}
