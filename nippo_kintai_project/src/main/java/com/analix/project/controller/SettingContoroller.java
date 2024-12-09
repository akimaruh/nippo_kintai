package com.analix.project.controller;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analix.project.entity.WorkSchedule;
import com.analix.project.form.WorkScheduleForm;
import com.analix.project.service.WorkScheduleService;

@Controller
public class SettingContoroller {

	@Autowired
	private WorkScheduleService workScheduleService;

	// 初期表示
	@RequestMapping("/setting")
	public String showSetting(Model model) {
		model.addAttribute("workScheduleForm", new WorkScheduleForm());
		return "/common/setting";
	}

	// 設定
	@PostMapping("/setting/workSchedule/save")
	public String saveWorkSchedule(@Validated @ModelAttribute WorkScheduleForm workScheduleForm, BindingResult result,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("workScheduleForm", workScheduleForm);
			model.addAttribute("error", "エラー内容に従って修正してください");
			return "/common/setting";
		}

		WorkSchedule schedule = new WorkSchedule();

		// Integer型のHourとMinuteをLocalTime型に変換
		LocalTime formattedStartTime = LocalTime.of(workScheduleForm.getStartTimeHour(),
				workScheduleForm.getStartTimeMinute());
		LocalTime formattedEndTime = LocalTime.of(workScheduleForm.getEndTimeHour(),
				workScheduleForm.getEndTimeMinute());

		schedule.setUserId(workScheduleForm.getUserId());
		schedule.setStartTime(formattedStartTime);
		schedule.setEndTime(formattedEndTime);
		schedule.setBreakTime(workScheduleForm.getBreakTime());

		// 既存データがあれば更新、なければ挿入
		workScheduleService.scheduleSaveOrUpdate(schedule);

		return "/common/setting";
	}

}
