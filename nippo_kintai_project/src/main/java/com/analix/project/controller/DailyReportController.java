package com.analix.project.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.analix.project.entity.Users;
import com.analix.project.form.DailyReportForm;
import com.analix.project.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;

	/**
	 * 初期表示
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/regist")
	public String showDailyReportRegistPage(@Validated @ModelAttribute DailyReportForm dailyReportForm,
			HttpSession session, Model model, @RequestParam(name = "date", required = false) String date) {

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);
		LocalDate targetDate;
		targetDate = LocalDate.now();

		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		model.addAttribute("statusName", statusName);

		//日報取得
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		model.addAttribute("dailyReport", dailyReportForm);

		return "/dailyReport/regist";
	}

	/**
	 * カレンダ－変更
	 * @param dailyReportForm
	 * @param session
	 * @param model
	 * @param date
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/change")
	public String changeDailyReportRegistPage(@Validated @ModelAttribute DailyReportForm dailyReportForm,
			HttpSession session, Model model, @RequestParam("date") String date) {

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);
		LocalDate targetDate;
		targetDate = LocalDate.parse(date);

		System.out.println("コントローラ" + targetDate);
		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		model.addAttribute("statusName", statusName);

		//日報取得
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		System.out.println("HTML直前2" + dailyReportForm);
		model.addAttribute("dailyReport", dailyReportForm);
		model.addAttribute(targetDate);

		return "/dailyReport/regist";
	}

	/**
	 * 『提出』ボタン押下後
	 * @param submittedDailyReportForm
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/regist/complete", method = RequestMethod.POST)
	public String submitDailyReport(@Validated @ModelAttribute DailyReportForm dailyReportForm,
			@RequestParam("userId") String userId, @RequestParam("date") String date,
			@RequestParam("id") String id, BindingResult result, HttpSession session) {
		String idAfterDecision = (id == "") ? "0" : id;
		dailyReportForm.setId(Integer.parseInt(idAfterDecision));
		dailyReportForm.setUserId(Integer.parseInt(userId));
		LocalDate targetDate = LocalDate.parse(date);
		dailyReportForm.setDate(targetDate);
		
		System.out.println("登録"+dailyReportForm);
		if (result.hasErrors()) {
			return "/dailyReport/regist"; // バリデーションエラーがあれば、再度フォームを表示
		}
		//内容取得→入力チェック→詰め替え→（登録→ステータス変更or更新→ステータスそのまま)→完了
		//		Users user =(Users) session.getAttribute("loginUser");
		//		Integer userId =user.getId(); 
		//入力チェック
		//		dailyReportService.validationForm(dailyReportForm);
		//登録or更新
		dailyReportService.registDailyReportService(dailyReportForm);
		//ステータスを提出済承認前に変更
		dailyReportService.updateDailyReportStatus(dailyReportForm);

		return "redirect:/dailyReport/regist";

	}
}
