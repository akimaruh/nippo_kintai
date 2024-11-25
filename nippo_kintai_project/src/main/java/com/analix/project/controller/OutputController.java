package com.analix.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.entity.Users;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.OutputService;
import com.analix.project.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class OutputController {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private UserService userService;
	@Autowired
	private OutputService outputService;

	/**
	 * 出力選択画面初期表示
	 * @param model
	 * @return 出力選択画面
	 */
	@RequestMapping(path = "/output/list")
	public String showOutputSelect(Model model) {
		LocalDate now = LocalDate.now();
		YearMonth currentYearMonth = YearMonth.of(now.getYear(), now.getMonthValue());
		Map<String, Integer> userNameAndIdMap = (Map<String, Integer>) model.asMap().get("userNameAndIdMap");

		model.addAttribute("userNameAndIdMap", userNameAndIdMap);
		model.addAttribute("targetYearMonth", currentYearMonth);

		return "/output/list";
	}

	/**
	 *ユーザー・対象年月を選択し『日報帳票』ボタン押下後
	 * @param userIdString
	 * @param targetYearMonth
	 * @param model
	 * @param redirectAttributes
	 * @return 日報帳票画面(web画面出力)
	 */
	@RequestMapping(path = "/output/dailyReportOutput")
	public String showDailyReportOutput(@RequestParam("userId") String userIdString,
			@RequestParam("targetYearMonth") YearMonth targetYearMonth, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {
		Integer userId = Integer.parseInt(userIdString);

		List<DailyReportDto> dailyReportList = dailyReportService
				.getDailyReportListForOutput(userId, targetYearMonth);

		if (dailyReportList.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "登録された日報がありません");
			return "redirect:/output/list";
		}
		Users selectedUserData = outputService.getselectedUserData(userId);
		DailyReportSummaryDto dailyReportSummaryDto = dailyReportService.getMonthlyDailyReportSummary(userId,
				targetYearMonth);

		session.setAttribute("dailyReportDtoList", dailyReportList);
		session.setAttribute("dailyReportSummaryDto", dailyReportSummaryDto);
		session.setAttribute("targetYearMonth", targetYearMonth);
		session.setAttribute("userData", selectedUserData);

		model.addAttribute("generateList", dailyReportList);
		model.addAttribute("dailyReportSummary", dailyReportSummaryDto);
		model.addAttribute("targetYearMonth", targetYearMonth);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", selectedUserData.getName());
		model.addAttribute("useBootstrap", true); // Bootstrapを使用する場合
		return "/output/dailyReportOutput";
	}

	/**
	 * 『Excel』ボタン押下後
	 * @param model
	 * @param response
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@PostMapping(path = "/output/dailyReportOutput/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void output(Model model, HttpServletResponse response, HttpSession session) throws IOException {
		outputService.downloadExcel(response, session);
		session.removeAttribute("dailyReportDtoList");
		session.removeAttribute("dailyReportSummaryDto");
		session.removeAttribute("targetYearMonth");
		session.removeAttribute("userData");
	}

}
