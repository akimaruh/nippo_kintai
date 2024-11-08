package com.analix.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
			RedirectAttributes redirectAttributes,HttpSession session) {
		Integer userId = Integer.parseInt(userIdString);
		List<DailyReportDto> dailyReportListByUserIdANDYearMonth = dailyReportService
				.getDailyReportListForOutput(userId, targetYearMonth);

		if (dailyReportListByUserIdANDYearMonth.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "登録された日報がありません");
			return "redirect:/output/list";
		}

		String userName = dailyReportListByUserIdANDYearMonth.getFirst().getUserName();
		DailyReportSummaryDto dailyReportSummaryDto = dailyReportService.getMonthlyDailyReportSummary(userId,
				targetYearMonth);
		
		session.setAttribute("dailyReportDtoList",dailyReportListByUserIdANDYearMonth);
		session.setAttribute("dailyReportSummaryDto", dailyReportSummaryDto);
		session.setAttribute("targetYearMonth", targetYearMonth);
		session.setAttribute("userId", userId);
		session.setAttribute("userName", userName);
		
		

		model.addAttribute("generateList", dailyReportListByUserIdANDYearMonth);
		model.addAttribute("dailyReportSummary", dailyReportSummaryDto);
		model.addAttribute("targetYearMonth", targetYearMonth);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("useBootstrap", true); // Bootstrapを使用する場合
		return "/output/dailyReportOutput";
	}

	@PostMapping(path = "/output/dailyReportOutput/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void output(HttpServletResponse response,HttpSession session) throws IOException {
		List<DailyReportDto> dailyReportDtoList=(List<DailyReportDto>) session.getAttribute("dailyReportDtoList");
		DailyReportSummaryDto dailyReportSummaryDto =(DailyReportSummaryDto) session.getAttribute("dailyReportSummaryDto");
		System.out.println(dailyReportDtoList);
		System.out.println(dailyReportSummaryDto);
		
		outputService.downloadExcel(dailyReportDtoList,dailyReportSummaryDto,response);
		// xlsx形式ブックの生成
		Workbook workbook = new XSSFWorkbook();
		// シートの生成
		Sheet sheet = workbook.createSheet("作業内容");
		
		// ヘッダーのスタイル
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);

		// データのスタイル
		CellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setAlignment(HorizontalAlignment.CENTER);
		dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);

		// 1. 表1のヘッダー行
		Row headerRow = sheet.createRow(1);
		String[] headers = { "日付", "曜日", "作業①", "時間①", "作業②", "時間②", "作業③", "時間③", "作業④", "時間④", "作業⑤", "時間⑤",
				"総作業時間" };
		for (int i = 1; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		// 2. 表1のデータ
		Object[][] data = {
				{ "11/1", "金", "製造", 1, "製造", 1, "製造", 1, "製造", 1, "単体テスト", 8, 12 },
				{ "11/2", "土", "製造", 2, "", "", "", "", "", "", "", "", 1 },
				{ "11/3", "日", "製造", 1, "", "", "", "", "", "", "", "", 1 },
				{ "11/4", "月", "単体テスト", 2, "製造", 2, "総合テスト", 4, "製造", 4, "総合テスト", 5, 58 },
				{ "11/5", "火", "単体テスト", 1, "製造", 1, "結合テスト", 1, "", "", "", "", 23 }
		};

		int rowIndex = 2;
		for (Object[] rowData : data) {
			Row row = sheet.createRow(rowIndex++);
			for (int i = 0; i < rowData.length; i++) {
				Cell cell = row.createCell(i);
				if (rowData[i] instanceof String) {
					cell.setCellValue((String) rowData[i]);
				} else if (rowData[i] instanceof Integer) {
					cell.setCellValue((Integer) rowData[i]);
				}
				cell.setCellStyle(dataStyle);
			}
		}
		

		// 3. 表2のヘッダー行
		Row summaryHeaderRow = sheet.createRow(rowIndex++);
		String[] summaryHeaders = { "総合テスト", "製造", "単体テスト", "結合テスト", "基本設計", "総作業時間" };
		for (int i = 0; i < summaryHeaders.length; i++) {
			Cell cell = summaryHeaderRow.createCell(i);
			cell.setCellValue(summaryHeaders[i]);
			cell.setCellStyle(headerStyle);
		}

		// 4. 表2のデータ
		Row summaryRow = sheet.createRow(rowIndex);
		Object[] summaryData = { 6, 58, 13, 6, 12, 95 };
		for (int i = 0; i < summaryData.length; i++) {
			Cell cell = summaryRow.createCell(i);
			cell.setCellValue((Integer) summaryData[i]);
			cell.setCellStyle(dataStyle);
		}

		// 列の幅を自動調整
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		

	}

}
