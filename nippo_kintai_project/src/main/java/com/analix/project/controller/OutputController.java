package com.analix.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.MonthlyAttendanceDto;
import com.analix.project.dto.MonthlyDailyReportDto;
import com.analix.project.entity.Users;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.OutputService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class OutputController {

	private final DailyReportService dailyReportService;
	private final OutputService outputService;

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
	 * @param userIdString || defaultValue ="0"
	 * @param targetYearMonth
	 * @param model
	 * @param redirectAttributes
	 * @return 日報帳票画面(web画面出力)
	 */
	@PostMapping(path = "/output/dailyReportOutput")
	public String showDailyReportOutput(@RequestParam(name = "userId", defaultValue = "0") String userIdString,
			@RequestParam("targetYearMonth") YearMonth targetYearMonth, @RequestParam("action") String action,
			Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {

		Integer userId = Integer.parseInt(userIdString);
		Users selectedUserData = outputService.getselectedUserData(userId);
		Integer employeeCode = selectedUserData == null ? null : selectedUserData.getEmployeeCode();
		MonthlyDailyReportDto monthlyDailyReportDto = new MonthlyDailyReportDto();
		List<MonthlyAttendanceDto> attendanceList = new ArrayList<>();

		if (action == "dailyReport") {
			monthlyDailyReportDto = outputService.getDailyReportListForOutput(userId, targetYearMonth);
			if (monthlyDailyReportDto == null) {
				storeErrorModel(targetYearMonth,employeeCode,model);
				return "/output/list";
			}
//			DailyReportSummaryDto dailyReportSummaryDto = monthlyDailyReportDto.getDailyReportSummaryDto();
			session.setAttribute("monthlyDailyReport", monthlyDailyReportDto);
//			session.setAttribute("dailyReportDtoList", monthlyDailyReportDto.getDailyReportDtoList());
//			session.setAttribute("dailyReportSummaryDto", dailyReportSummaryDto);
			model.addAttribute("monthlyDailyReport", monthlyDailyReportDto);
			//model.addAttribute("generateList", monthlyDailyReportDto.getDailyReportDtoList());
			//model.addAttribute("dailyReportSummary", dailyReportSummaryDto);
		}
		if (action == "attendance") {

		}
		if (action == "attendanceDailyReport") {

		}

		//Excel出力で利用するためセッション格納

		session.setAttribute("targetYearMonth", targetYearMonth);
		session.setAttribute("userData", selectedUserData);

		//pdf出力時セッションのパラメータをThymeleafで反映できないためモデルに格納

		model.addAttribute("targetYearMonth", targetYearMonth);
		model.addAttribute("userData", selectedUserData);
		model.addAttribute("useBootstrap", true); // Bootstrapを使用する場合
		return "/output/dailyReportOutput";

	}

	public void storeErrorModel(YearMonth targetYearMonth, Integer employeeCode, Model model) {

		model.addAttribute("error", "登録された日報がありません");
		model.addAttribute("targetYearMonth", targetYearMonth);
		//ロード時にJSのuserSearch()を利用してユーザー名を表示させる
		model.addAttribute("userKeyword", employeeCode);
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
