package com.analix.project.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.entity.WorkSchedule;
import com.analix.project.form.WorkScheduleForm;
import com.analix.project.service.WorkScheduleCacheService;
import com.analix.project.service.WorkScheduleService;
import com.analix.project.util.SessionHelper;

@Controller
public class SettingContoroller {

	@Autowired
	private WorkScheduleService workScheduleService;
	@Autowired
	private SessionHelper sessionHelper;
	@Autowired
	private WorkScheduleCacheService workScheduleCacheService;

	/**
	 * 初期表示
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting")
	public String showSetting(Model model) {
		
		Integer userId = sessionHelper.getUser().getId();
		
		// Model から Flash attribute を取得
		WorkScheduleForm workScheduleForm = (WorkScheduleForm) model.getAttribute("workScheduleForm");
		// nullの場合=初期表示やエラーがなくリダイレクトされてきた場合はデータベースの情報を表示
		if (workScheduleForm == null) {
			workScheduleForm = workScheduleService.getWorkSchedule(userId);
			if(workScheduleForm == null) {
				workScheduleForm = new WorkScheduleForm();
			}
		}
		model.addAttribute("workScheduleForm", workScheduleForm);
		
	    // リダイレクト後に入力チェックのエラーがあればモデルに渡す
		BindingResult result = (BindingResult) model.getAttribute("org.springframework.validation.BindingResult.workScheduleForm");
	    if (result != null && result.hasErrors()) {
	        model.addAttribute("org.springframework.validation.BindingResult.workScheduleForm", result);
	    }
		
		// プルダウン用0~23の時間リスト
		List<Integer> hours = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			hours.add(i);
		}
		model.addAttribute("hours", hours);

		return "/common/setting";
	}

	/**
	 * 保存ボタン押下
	 * @param workScheduleForm
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping("/setting/workSchedule/save")
	public String saveWorkSchedule(@Validated @ModelAttribute WorkScheduleForm workScheduleForm, BindingResult result,
			RedirectAttributes redirectAttributes) {
		
		// 入力チェック
		workScheduleService.validationForm(workScheduleForm, result);
		
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("workScheduleForm", workScheduleForm);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.workScheduleForm", result);
			redirectAttributes.addFlashAttribute("error", "エラー内容に従って修正してください。");
			return "redirect:/setting";
		}

		// Integer型のHourとMinuteをLocalTime型に変換
		LocalTime formattedStartTime = LocalTime.of(workScheduleForm.getStartTimeHour(),workScheduleForm.getStartTimeMinute());
		LocalTime formattedEndTime = LocalTime.of(workScheduleForm.getEndTimeHour(),workScheduleForm.getEndTimeMinute());
		
		// formからentityに入れなおし
		WorkSchedule schedule = new WorkSchedule();
		schedule.setUserId(workScheduleForm.getUserId());
		schedule.setStartTime(formattedStartTime);
		schedule.setEndTime(formattedEndTime);
		schedule.setBreakTime(workScheduleForm.getBreakTime());

		// 既存データがあれば更新、なければ登録
		boolean isSuccess = saveOrUpdateSchedule(schedule);
		String message = isSuccess ? "勤務時間の設定が完了しました。" : "設定に失敗しました。";
		
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/setting";
	}
	
	/**
	 * 既存データがあれば更新、なければ登録
	 * @param schedule
	 * @return ture成功 false失敗
	 */
	@Transactional
	public boolean saveOrUpdateSchedule(WorkSchedule schedule) {
		boolean isSuccess;
		if (workScheduleService.existsByUserId(schedule.getUserId())) {
			isSuccess = workScheduleService.updateWorkSchedule(schedule);
		} else {
			isSuccess = workScheduleService.saveWorkSchedule(schedule);
		}
		
		// 成功していたらキャッシュの更新もする
		if (isSuccess) {
			workScheduleCacheService.put(schedule.getUserId(), schedule);
		}
		
		return isSuccess;
	}

}
