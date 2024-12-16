package com.analix.project.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analix.project.dto.NotificationsDto;
import com.analix.project.entity.Users;
import com.analix.project.mapper.NotificationsMapper;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.InformationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Controller
public class StartMenuController {
	
	public final AttendanceService attendanceService;
	public final InformationService informationService;
	public final NotificationsMapper notificationsMapper;
	public final DailyReportService dailyReportService;

	/**
	 * 初期表示
	 * @param session
	 * @param model
	 * @return 処理メニュー画面
	 */
	@RequestMapping(path = "/common/startMenu")
	public String showMenu(HttpSession session, String modal, Model model) {
		
		Users user = (Users) session.getAttribute("loginUser");
		LocalDate today = LocalDate.now();

		
		List<NotificationsDto> notificationsDtoList = informationService.findNotification(user.getId());
		//打刻ボタン活性チェック
		boolean isStampingCheck = attendanceService.findTodaysStartTime(user.getId(), today);
		//作業プルダウンのデータ取得
		Map<String, Integer> workMap = dailyReportService.pulldownWork(user.getId());
		
		model.addAttribute("notificationsList", notificationsDtoList);
		if (notificationsDtoList.isEmpty()) {
			model.addAttribute("notificationsListMessage", "--現在、おしらせはありません。--");
		}
		model.addAttribute("isStampingCheck", isStampingCheck);
		model.addAttribute("workMap", workMap);
		return "common/startMenu";
	}

//	/**
//	 * 『ログオフ』ボタン押下後
//	 * @return ページを閉じる
//	 */
//	@RequestMapping(path = "/common/logoff", method = RequestMethod.POST)
//	public String logoff(HttpServletRequest request, HttpServletResponse response,
//			RedirectAttributes redirectAttributes) {
//
//		HttpSession session = request.getSession(false);
//		if (session != null) {
//			session.invalidate();
//		}
//		redirectAttributes.addFlashAttribute("message", "ログオフしました。再度ログインしてください。");
//
//		return "redirect:/";
//	}
	
	// パスワード再設定確認用　あとでけす
	@GetMapping("/password/regist")
	public String passwordRegist() {
		return "/password/regist";
	}

}
