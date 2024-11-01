package com.analix.project.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.UserService;

@Controller
public class OutputController {

	@Autowired
	private DailyReportService dailyReportService;
		@Autowired
		private UserService userService;

	@RequestMapping(path = "/output/list")
	public String showOutputSelect(Model model) {
		LocalDate now = LocalDate.now();
		YearMonth currentYearMonth = YearMonth.of(now.getYear(), now.getMonthValue());
		Map<String, Integer> userNameAndIdMap = (Map<String, Integer>) model.asMap().get("userNameAndIdMap");

		model.addAttribute("userNameAndIdMap", userNameAndIdMap);
		model.addAttribute("targetYearMonth", currentYearMonth);
		
		return "/output/list";
	}

	@RequestMapping(path = "/output/dailyReportOutput")
	public String showDailyReportOutput(@RequestParam("userId") String userIdString,
			@RequestParam("targetYearMonth") YearMonth targetYearMonth, Model model,RedirectAttributes redirectAttributes) {
		Integer userId = Integer.parseInt(userIdString);
		List<DailyReportDto> dailyReportListByUserIdANDYearMonth = dailyReportService
				.getDailyReportListForOutput(userId, targetYearMonth);
		
		if(dailyReportListByUserIdANDYearMonth.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","登録された日報がありません");
			return "redirect:/output/list";
		}
		String userName = dailyReportListByUserIdANDYearMonth.getFirst().getUserName();
		DailyReportSummaryDto dailyReportSummaryDto = dailyReportService.getMonthlyDailyReportSummary(userId,
				targetYearMonth);
		
		model.addAttribute("generateList", dailyReportListByUserIdANDYearMonth);
		model.addAttribute("dailyReportSummary", dailyReportSummaryDto);
		model.addAttribute("targetYearMonth", targetYearMonth);
		model.addAttribute("userId", userId);
		model.addAttribute("userName",userName);
		model.addAttribute("useBootstrap", true); // Bootstrapを使用する場合
		return "/output/dailyReportOutput";
	}

	//	@RequestMapping(path = "/output/dailyReportOutput/pdf/pdf")
	//	public String showDailyReportOutputpdf(@RequestParam("userId") String userIdString,
	//			@RequestParam("targetYearMonth") YearMonth targetYearMonth, Model model,
	//			@RequestParam("image") String imageData) {
	//		Integer userId = Integer.parseInt(userIdString);
	//
	//		// Base64データからプレフィックスを削除
	//		String base64Image = imageData.split(",")[1];
	//		String imageHtml = "data:image/png;base64," + base64Image;
	//		model.addAttribute("image", imageHtml);
	//
	//		List<DailyReportDto> dailyReportListByUserIdANDYearMonth = dailyReportService
	//				.getDailyReportListForOutput(userId, targetYearMonth);
	//		DailyReportSummaryDto dailyReportSummaryDto = dailyReportService.getMonthlyDailyReportSummary(userId,
	//				targetYearMonth);
	//		System.out.println(dailyReportListByUserIdANDYearMonth);
	//		model.addAttribute("generateList", dailyReportListByUserIdANDYearMonth);
	//		model.addAttribute("dailyReportSummary", dailyReportSummaryDto);
	//		model.addAttribute("targetYearMonth", targetYearMonth);
	//		model.addAttribute("userId", userId);
	//		model.addAttribute("useBootstrap", true); // Bootstrapを使用する場合
	//		return "/output/dailyReportOutput";
	//	}
	@GetMapping(path = "/output/userSearch")
	public String serachForOutputUser(@RequestParam("userKeyword") String userKeyword,
			RedirectAttributes redirectAttributes) {		
			Map<String, Integer> userNameAndIdMap = userService.searchForUserNameAndId(userKeyword);
			redirectAttributes.addFlashAttribute("userNameAndIdMap", userNameAndIdMap);
			redirectAttributes.addFlashAttribute("userKeyword", userKeyword);
			return "redirect:/output/list";
	}

}
