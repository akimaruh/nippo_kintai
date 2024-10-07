package com.analix.project.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.Users;
import com.analix.project.form.DailyReportForm;
import com.analix.project.service.DailyReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	public String showDailyReportRegistPage(@ModelAttribute DailyReportForm dailyReportForm,
			HttpSession session, Model model) {
		System.out.println("初期表示状態：" + dailyReportForm);
		// ヘッダー部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);

		// Model から Flash attribute を取得(提出完了後処理)
		LocalDate targetDate = (LocalDate) model.getAttribute("targetDate");
		dailyReportForm = (DailyReportForm) model.getAttribute("dailyReportForm");
		System.out.println("FlashAttribute：" + dailyReportForm);
		System.out.println("FlashAttribute：" + targetDate);

		// targetDate が null の場合、デフォルトとして今日の日付を使用
		if (targetDate == null) {
			targetDate = LocalDate.now();
		}
		model.addAttribute("targetDate", targetDate);

		//日報取得
		if (dailyReportForm.getDailyReportFormDetailList() == null) {
			dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		}
		model.addAttribute("dailyReport", dailyReportForm);
		System.out.println("モデルに入れた：" + dailyReportForm);
		System.out.println("モデルに入れた：" + targetDate);
		//ステータス
		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		model.addAttribute("statusName", statusName);
		forgetReportRegist();

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
	@GetMapping(path = "/dailyReport/change")
	public String changeDailyReportRegistPage(@ModelAttribute DailyReportForm dailyReportForm,
			HttpSession session, Model model, @RequestParam("date") String date,RedirectAttributes redirectAttributes)  {

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);
		
		LocalDate targetDate;
		//パースエラーの場合強制で初期表示に戻る
		try {
		targetDate = LocalDate.parse(date);
		}catch(Exception e){
			
			redirectAttributes.addFlashAttribute("error","今日以前の日付を選択してください。");
			return "redirect:/dailyReport/regist" ;
		}

		System.out.println("コントローラ" + targetDate);
		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		model.addAttribute("statusName", statusName);

		//日報取得
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		System.out.println("HTML直前2" + dailyReportForm);
		model.addAttribute("dailyReport", dailyReportForm);
		model.addAttribute("targetDate", targetDate);

		return "/dailyReport/regist";
	}

	/**
	 * 『提出』ボタン押下後
	 * @param submittedDailyReportForm
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/regist/complete", method = RequestMethod.POST)
	public String submitDailyReport(@Valid @ModelAttribute("dailyReport") DailyReportForm dailyReportForm,
			BindingResult result,
			HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		System.out.println("フォーム受け取りほやほや" + dailyReportForm);
		//		String idAfterDecision = (id == "") ? "0" : id;
		//		dailyReportForm.setId(Integer.parseInt(idAfterDecision));
		//		dailyReportForm.setUserId(Integer.parseInt(userId));
		//		LocalDate targetDate = LocalDate.parse(date);
		//		dailyReportForm.setDate(targetDate);
		LocalDate targetDate = dailyReportForm.getDate();
		boolean isRegistComplete=false;

		if (result.hasErrors()) {
			model.addAttribute("targetDate", targetDate);
			model.addAttribute("dailyReport", dailyReportForm);
			model.addAttribute("error", "日報の登録が失敗しました。");

			return "/dailyReport/regist"; // バリデーションエラーがあれば、再度フォームを表示
		}

		System.out.println("登録" + dailyReportForm);

		
		isRegistComplete = dailyReportService.registDailyReportService(dailyReportForm);
		//ステータスを提出済承認前に変更
		 dailyReportService.updateDailyReportStatus(dailyReportForm);

		redirectAttributes.addFlashAttribute("targetDate", targetDate);
		if (isRegistComplete = true) {
			redirectAttributes.addFlashAttribute("message", "日報の登録が完了しました。");
		} else if(isRegistComplete = false){
			redirectAttributes.addFlashAttribute("error", "日報の登録が失敗しました。");
		}
		return "redirect:/dailyReport/regist";

	}
	
	//お試しバッチ処理準備
	public void forgetReportRegist() {
		dailyReportService.registCheck();
	}
}
