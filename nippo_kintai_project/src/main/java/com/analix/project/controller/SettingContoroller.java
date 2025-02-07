package com.analix.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.UserWorkVisibilityDto;
import com.analix.project.form.UserWorkVisibilityListForm;
import com.analix.project.form.WorkScheduleForm;
import com.analix.project.service.WorkScheduleService;
import com.analix.project.service.WorkService;
import com.analix.project.util.SessionHelper;

@Controller
public class SettingContoroller {

	@Autowired
	private WorkScheduleService workScheduleService;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private WorkService workService;

	/**
	 * 初期表示
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting")
	public String showSetting(Model model) {

		Integer userId = sessionHelper.getUser().getId();

		// 【勤怠】勤務時間の設定
		// Model から Flash attribute を取得
		WorkScheduleForm workScheduleForm = (WorkScheduleForm) model.getAttribute("workScheduleForm");
		// nullの場合=初期表示やエラーがなくリダイレクトされてきた場合はデータベースの情報を表示
		if (workScheduleForm == null) {
			workScheduleForm = workScheduleService.getWorkSchedule(userId);
			if (workScheduleForm == null) {
				workScheduleForm = new WorkScheduleForm();
			}
		}
		model.addAttribute("workScheduleForm", workScheduleForm);

		// リダイレクト後に入力チェックのエラーがあればモデルに渡す
		BindingResult result = (BindingResult) model
				.getAttribute("org.springframework.validation.BindingResult.workScheduleForm");
		if (result != null && result.hasErrors()) {
			model.addAttribute("org.springframework.validation.BindingResult.workScheduleForm", result);
		}

		// プルダウン用0~23の時間リスト
		List<Integer> hours = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			hours.add(i);
		}
		model.addAttribute("hours", hours);

		// 【日報】作業一覧リスト
		List<UserWorkVisibilityDto> workVisibilityList = workService.getVisibilityWorkList(userId);
		UserWorkVisibilityListForm workListForm = new UserWorkVisibilityListForm();
		workListForm.setWorkVisibilityList(workVisibilityList);
		model.addAttribute("workListForm", workListForm);

		return "/common/setting";
	}

	/**
	 * 【勤怠】保存ボタン押下
	 * @param workScheduleForm
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/setting/workSchedule/save")
	public String saveWorkSchedule(@Validated @ModelAttribute WorkScheduleForm workScheduleForm, BindingResult result,
			RedirectAttributes redirectAttributes) {

		// 入力チェック
		workScheduleService.validationForm(workScheduleForm, result);

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("workScheduleForm", workScheduleForm);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.workScheduleForm",
					result);
			redirectAttributes.addFlashAttribute("workError", "エラー内容に従って修正してください。");
			return "redirect:/setting";
		}

		boolean isSuccess = workScheduleService.saveWorkSchedule(workScheduleForm);
		if (isSuccess) {
			redirectAttributes.addFlashAttribute("workMessage", "勤務時間の設定が完了しました。");
		} else {
			redirectAttributes.addFlashAttribute("workError", "設定に失敗しました。");
		}

		return "redirect:/setting";
	}

	/**
	 * 【日報】保存ボタン押下（社員権限）
	 * @param workListForm
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/setting/work/save")
	public String saveWorkList(@ModelAttribute UserWorkVisibilityListForm workListForm,
			RedirectAttributes redirectAttributes) {

		Integer userId = sessionHelper.getUser().getId();
		boolean isSuccess = workService.saveWorkList(workListForm, userId);

		if (isSuccess) {
			redirectAttributes.addFlashAttribute("dailyReportMessage", "設定が完了しました。");
		} else {
			redirectAttributes.addFlashAttribute("dailyReportError", "設定に失敗しました。");
		}

		return "redirect:/setting";
	}

	/**
	 * 【日報】登録ボタン押下（マネージャ権限）
	 * @param workName
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/setting/work/regist")
	public String registWork(@RequestParam("workName") String workName, RedirectAttributes redirectAttributes) {

		// 入力チェック
		String errorMessage = workService.validateWorkName(workName);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("dailyReportError", errorMessage);
			redirectAttributes.addFlashAttribute("workName", workName);
			return "redirect:/setting";
		}

		boolean isSuccess = workService.insertWork(workName);
		if (isSuccess) {
			redirectAttributes.addFlashAttribute("dailyReportMessage", workName + "を追加しました。");
		} else {
			redirectAttributes.addFlashAttribute("dailyReportError", "登録に失敗しました。");
		}

		return "redirect:/setting";
	}

}
