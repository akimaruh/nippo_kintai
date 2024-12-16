package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.WorkSchedule;
import com.analix.project.service.LoginService;
import com.analix.project.service.WorkScheduleCacheService;
import com.analix.project.service.WorkScheduleService;
import com.analix.project.util.SessionHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Controller
public class LoginController {

	private final LoginService loginService;
	private final SessionHelper sessionHelper;
	private final WorkScheduleService workScheduleService;
	private final WorkScheduleCacheService workScheduleCacheService;

	@GetMapping("/")
	public String getLogin(Model model, HttpSession session) {
		return "common/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("employeeCode") String employeeCode, @RequestParam("password") String password,
			HttpSession session, Model model) {
		System.out.println(sessionHelper.getUser());
		String loginResult = loginService.handleLogin(employeeCode, password);

		// 勤務時間をキャッシュに追加
		WorkSchedule workSchedule = workScheduleService.getWorkScheduleEntity(sessionHelper.getUser().getId());
		if (workSchedule != null) {
			workScheduleCacheService.put(sessionHelper.getUser().getId(), workSchedule);
		}

		if (loginResult.equals("error")) {
			model.addAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "common/login";
		}
		//ログイン通過
		////ログイン完了後遷移(コメントアウト中)
		////権限による画面遷移の可能性を考え、現状権限で分岐する書き方で進める
		//		if ("Admin".equals(role)) {
		//			return "redirect:/common/startMenu";
		//		} else if ("UnitManager".equals(role) || "Manager".equals(role) || "Regular".equals(role)) {
		//			return "redirect:/common/startMenu";
		//		}
		//	}
		return "redirect:/common/startMenu";
	}

	/**
	 * 『ログオフ』ボタン押下後
	 * @return ログイン画面遷移
	 */
	@RequestMapping(path = "/common/logoff", method = RequestMethod.POST)
	public String logoff(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		if (sessionHelper != null) {
			workScheduleCacheService.remove(sessionHelper.getUser().getId()); // キャッシュから勤務時間削除
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		redirectAttributes.addFlashAttribute("message", "ログオフしました。再度ログインしてください。");

		return "redirect:/";
	}

	/**
	 * タイムアウト時
	 * @param model
	 * @param session
	 * @return ログイン画面遷移
	 */
	@GetMapping("/timeout")
	public String timeout(Model model, HttpSession session) {

		session.invalidate(); // セッションを無効化
		model.addAttribute("message", "タイムアウトしました。再ログインしてください。");
		return "common/login";
	}

}