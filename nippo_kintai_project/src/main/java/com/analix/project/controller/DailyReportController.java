package com.analix.project.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.analix.project.entity.Users;
import com.analix.project.form.DailyReportDetailForm;
import com.analix.project.form.DailyReportForm;
import com.analix.project.form.DailyReportGroup;
import com.analix.project.form.StartMenuDailyReportGroup;
import com.analix.project.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;

	/**
	 * 初期表示
	 * @param dailyReportForm
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/regist")
	public String showDailyReportRegistPage(
			@ModelAttribute DailyReportForm dailyReportForm,
			HttpSession session,
			Model model) {

		// ヘッダー部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);

		// Model から Flash attribute を取得(提出完了後処理)
		LocalDate targetDate = (LocalDate) model.getAttribute("targetDate");
		dailyReportForm = (DailyReportForm) model.getAttribute("dailyReportForm");

		// targetDate が null の場合、デフォルトとして今日の日付を使用
		targetDate = (targetDate != null) ? targetDate : LocalDate.now();
		model.addAttribute("targetDate", targetDate);

		// 日報取得
		//	    if (dailyReportForm.getDailyReportFormDetailList() == null) {
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		//	    }

		// モデルにデータを追加
		addModelAttributes(model, dailyReportForm, userId, targetDate);

		return "/dailyReport/regist";
	}

	/**
	 * 基本表示内容をモデルに追加
	 * @param model
	 * @param workMap
	 * @param dailyReportForm
	 * @param userId
	 * @param targetDate
	 */
	private void addModelAttributes(Model model,
			DailyReportForm dailyReportForm,
			Integer userId, LocalDate targetDate) {

		// ステータスの取得
		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		System.out.println(statusName);
		//作業プルダウンのデータ取得
		Map<String, Integer> workMap = dailyReportService.pulldownWork();

		model.addAttribute("statusName", statusName);
		model.addAttribute("dailyReport", dailyReportForm);
		model.addAttribute("workMap", workMap);
		model.addAttribute("targetDate", targetDate);
		
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
			HttpSession session, Model model, @RequestParam("date") LocalDate targetDate,
			RedirectAttributes redirectAttributes) {

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		model.addAttribute(userId);
		// 日報取得
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		// モデルにデータを追加
		addModelAttributes(model, dailyReportForm, userId, targetDate);

		return "/dailyReport/regist";
	}

	/**
	 * 『提出』ボタン押下後
	 * @param submittedDailyReportForm
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/regist/complete", method = RequestMethod.POST)
	public String submitDailyReport(
			@Validated(DailyReportGroup.class) @ModelAttribute("dailyReport") DailyReportForm dailyReportForm,
			BindingResult result,
			HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		System.out.println("フォーム受け取りほやほや" + dailyReportForm);
		//		String idAfterDecision = (id == "") ? "0" : id;
		//		dailyReportForm.setId(Integer.parseInt(idAfterDecision));
		//		dailyReportForm.setUserId(Integer.parseInt(userId));
		//		LocalDate targetDate = LocalDate.parse(date);
		//		dailyReportForm.setDate(targetDate);
		LocalDate targetDate = dailyReportForm.getDate();
		boolean isRegistComplete = false;

		//		// バリデーショングループの指定
		//		validator.validate(dailyReportForm, result, DailyReportGroup.class);
		if (result.hasErrors()) {
			// モデルにデータを追加
			addModelAttributes(model, dailyReportForm, dailyReportForm.getUserId(), targetDate);

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
		} else if (isRegistComplete = false) {
			redirectAttributes.addFlashAttribute("error", "日報の登録が失敗しました。");
		}
		return "redirect:/dailyReport/regist";

	}

	/**
	 * 処理メニューから日報登録
	 * @param dailyReportdetailForm
	 * @param result
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */

	@RequestMapping(path = "/startMenu/dailyReport/submitComplete", method = RequestMethod.POST)
	public String submitDailyReportAtStartMenu(
			@Validated(StartMenuDailyReportGroup.class) @ModelAttribute("dailyReportDetailForm") DailyReportDetailForm dailyReportdetailForm,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		// エラーメッセージを格納するMap
		Map<String, String> errorMessages = new HashMap<>();

		// フィールドごとのエラーメッセージを収集
		result.getFieldErrors().forEach(error -> {
			errorMessages.put(error.getField(), error.getDefaultMessage());
		});

		//入力チェック(お知らせ見える版)
		if (result.hasErrors()) {

			//作業プルダウンのデータ取得
			//			Map<String, Integer> workMap = dailyReportService.pulldownWork();
			//			redirectAttributes.addFlashAttribute("workMap", workMap);
			redirectAttributes.addFlashAttribute("dailyReportDetailForm", dailyReportdetailForm);
			redirectAttributes.addFlashAttribute("modalError", "日報の登録に失敗しました。");

			redirectAttributes.addFlashAttribute("openModal", true);
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);
			System.out.println("入力チェックエラーあり");
			System.out.println(result.getAllErrors());
			//再度モーダルを開きエラー内容表示
			return "redirect:/common/startMenu";
		}
		//		//入力チェック(お知らせが消える版)
		//		if (result.hasErrors()) {
		//			String modal = "modal";
		//			//作業プルダウンのデータ取得
		//			Map<String, Integer> workMap = dailyReportService.pulldownWork();
		//			model.addAttribute("workMap", workMap);
		//			model.addAttribute("dailyReportDetailForm", dailyReportdetailForm);
		//			model.addAttribute("modal", modal);
		//			//再度モーダルを開きエラー内容表示
		//			return "/common/startMenu";
		//		}
		//本日の日付を取得
		LocalDate today = LocalDate.now();
		dailyReportdetailForm.setDate(today);
		//通常の日報登録方法用に変換＆日報登録
		boolean isRegistAtStartMenu = dailyReportService.registDailyReportAtStartMenu(dailyReportdetailForm);

		if (isRegistAtStartMenu) {
			redirectAttributes.addFlashAttribute("reportMessage", "日報の登録が完了しました。");
		} else {
			redirectAttributes.addFlashAttribute("reportError", "日報の登録に失敗しました。");
		}
		System.out.println("提出完了");
		return "redirect:/common/startMenu";

	}

	/**
	 * 日報確認画面初期表示
	 * @param dailyReportForm
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/list")
	public String showDailyReportListPage(Model model) {
		// Model から Flash attribute を取得(提出完了後処理)
		LocalDate today =  LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		LocalDate targetDate = (LocalDate) model.getAttribute("targetDate");
		// targetDate が null の場合、デフォルトとして前日の日付を使用
		targetDate = (targetDate != null) ? targetDate : yesterday;
		model.addAttribute("targetDate", targetDate);
		model.addAttribute("today", today);
		addModelAttributes(model, targetDate);

		return "/dailyReport/list";
	}

	/**
	 * カレンダー変更
	 * @param model
	 * @param targetDate
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/list/change")
	public String changeDailyReportListPage(Model model,
			@RequestParam("date") LocalDate targetDate) {
		LocalDate today = LocalDate.now();
		model.addAttribute("today", today);
		addModelAttributes(model, targetDate);
		return "/dailyReport/list";
	}

	/**
	 * 基本表示内容をモデルに追加
	 * @param model
	 * @param targetDate
	 */
	public void addModelAttributes(Model model, LocalDate targetDate) {
		List<DailyReportForm> dailyReportFormList = dailyReportService.getDailyReportList(targetDate);
		model.addAttribute("dailyReportFormList", dailyReportFormList);
		model.addAttribute("targetDate", targetDate);
	}

	/**
	 * 日報承認画面『承認』ボタン押下後
	 * @param userId
	 * @param targetDate
	 * @return
	 */
	@RequestMapping(path = "/dailyReport/list/approve", method = RequestMethod.POST)
	public String approveDailyReport(@RequestParam("userId") Integer userId,
			@RequestParam("targetDate") LocalDate targetDate, RedirectAttributes redirectAttributes) {

		dailyReportService.approveDailyReport(userId, targetDate);
		redirectAttributes.addFlashAttribute("targetDate", targetDate);
		return "redirect:/dailyReport/list";
	}

}
