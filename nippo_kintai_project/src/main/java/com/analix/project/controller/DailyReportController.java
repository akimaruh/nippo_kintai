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
import com.analix.project.util.CustomDateUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private CustomDateUtil customDateUtil;

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
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);

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
			HttpSession session, Model model, @RequestParam("date") String date,
			RedirectAttributes redirectAttributes) {

		// ヘッダー:ステータス部分
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
//		model.addAttribute(userId);

		LocalDate targetDate;
		
		//パースエラーの場合強制で初期表示に戻る
		try {
			targetDate = LocalDate.parse(date);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "今日以前の日付を選択してください。");
			return "redirect:/dailyReport/regist";
		}

		String statusName = dailyReportService.findStatusByUserId(userId, targetDate);
		model.addAttribute("statusName", statusName);

		//日報取得
		dailyReportForm = dailyReportService.getDailyReport(userId, targetDate);
		// モデルにデータを追加
		addModelAttributes(model, dailyReportForm, userId, targetDate);

		return "/dailyReport/regist";
	}

	/**
	 * 『提出』ボタン押下後
	 * @param dailyReportForm
	 * @param result
	 * @param session
	 * @param redirectAttributes
	 * @param model
	 * @return
	 */

	@RequestMapping(path = "/dailyReport/regist/complete", method = RequestMethod.POST)
	public String submitDailyReport(
			@Validated(DailyReportGroup.class) @ModelAttribute("dailyReport") DailyReportForm dailyReportForm,
			BindingResult result,
			HttpSession session, RedirectAttributes redirectAttributes, Model model) {
		boolean isRegistComplete = false;

		LocalDate targetDate = dailyReportForm.getDate();
		//登録完了チェックを初期化
		//		boolean isRegistComplete = false;

		if (result.hasErrors()) {
			// モデルにデータを追加
			addModelAttributes(model, dailyReportForm, dailyReportForm.getUserId(), targetDate);
			model.addAttribute("error", "日報の登録が失敗しました。");
			return "/dailyReport/regist"; // バリデーションエラーがあれば、再度フォームを表示
		}
		//日報登録
		try {
			isRegistComplete = dailyReportService.registDailyReportService(dailyReportForm);
		} catch (Exception e) {
			System.out.println("トランザクション内でエラーが発生しました: " + e.getMessage());
			isRegistComplete = false;
		}

		if (isRegistComplete == true) {
			System.out.println("isRegistComplete = true通過");
			redirectAttributes.addFlashAttribute("message",
					customDateUtil.dateHyphenSlashConverter(targetDate) + "の日報の登録が完了しました。");
		} else if (isRegistComplete == false) {
			System.out.println("isRegistComplete = false通過");
			redirectAttributes.addFlashAttribute("error",
					customDateUtil.dateHyphenSlashConverter(targetDate) + "日報の登録が失敗しました。");
		}
		redirectAttributes.addFlashAttribute("targetDate", targetDate);
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
		Map<String, String> errorMessages = new HashMap<>();
		// フィールドごとのエラーメッセージを収集
		result.getFieldErrors().forEach(error -> {
			errorMessages.put(error.getField(), error.getDefaultMessage());
		});
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("dailyReportDetailForm", dailyReportdetailForm);
			redirectAttributes.addFlashAttribute("modalError", "日報の登録に失敗しました。");
			redirectAttributes.addFlashAttribute("openModal", true);
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

			//再度モーダルを開きエラー内容表示
			return "redirect:/common/startMenu";
		}

		LocalDate today = LocalDate.now();
		dailyReportdetailForm.setDate(today);
		//通常の日報登録方法用に変換＆日報登録
		boolean isRegistAtStartMenu = dailyReportService.registDailyReportAtStartMenu(dailyReportdetailForm);

		if (isRegistAtStartMenu) {
			redirectAttributes.addFlashAttribute("reportMessage",
					customDateUtil.dateHyphenSlashConverter(today) + "の日報の登録が完了しました。");
		} else {
			redirectAttributes.addFlashAttribute("reportError",
					customDateUtil.dateHyphenSlashConverter(today) + "日報の登録に失敗しました。");
		}
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
	
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		LocalDate targetDate = (LocalDate) model.getAttribute("targetDate");
		// targetDate が null の場合、デフォルトとして前日の日付を使用
		//承認処理を行えるのは当日より前の日報のため初期表示は前日とする
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
			@RequestParam("date") String date, RedirectAttributes redirectAttributes) {
		LocalDate today = LocalDate.now();
		LocalDate targetDate;
		//パースエラーの場合強制で初期表示に戻る
		try {
			targetDate = LocalDate.parse(date);
		} catch (Exception e) {

			redirectAttributes.addFlashAttribute("error", "今日以前の日付を選択してください。");
			return "redirect:/dailyReport/list";
		}

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
