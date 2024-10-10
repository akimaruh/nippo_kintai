package com.analix.project.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.NotificationsDto;
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

		Users user = (Users) session.getAttribute("loginUser");
		List<NotificationsDto> notificationsDtoList = new ArrayList<>();
		notificationsDtoList = informationService.findNotification(user.getId());
		model.addAttribute("notificationsList", notificationsDtoList);
		if (notificationsDtoList.isEmpty()) {
			model.addAttribute("notificationsListMessage", "--現在、おしらせはありません。--");
		}
		LocalDate today = LocalDate.now();
		//打刻ボタン活性チェック
		boolean isStampingCheck = attendanceService.findTodaysStartTime(user.getId(),today);
		model.addAttribute("isStampingCheck",isStampingCheck);

		return "common/startMenu";
	}

	/**
	 * 『ログオフ』ボタン押下後
	 * @return ページを閉じる
	 */
	@RequestMapping(path = "/common/logoff", method = RequestMethod.POST)
	public String logoff(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		redirectAttributes.addFlashAttribute("message", "ログオフしました。再度ログインしてください。");

		return "redirect:/";
	}

}
