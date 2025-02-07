package com.analix.project.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.analix.project.dto.AttendanceReportDto;
import com.analix.project.dto.MonthlyAttendanceDto;
import com.analix.project.dto.MonthlyDailyReportDto;
import com.analix.project.entity.Users;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.servlet.http.HttpSession;

//@RequiredArgsConstructor
@Controller
public class PdfController {
	@Autowired
	private SpringTemplateEngine templateEngine;

	/**
	 * (共通部分) PDF化実施
	 * @param templatePath
	 * @param variables
	 * @param pdfFileName
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<InputStreamResource> generatePdfFile(String templatePath, Map<String, Object> variables,
			String pdfFileName)
			throws IOException {
		// コンテキスト作成	
		Context context = new Context();
		variables.forEach(context::setVariable);

		// テンプレートのHTML生成
		String html = templateEngine.process("output/" + templatePath, context);

		// HTMLをPDFに変換
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfRendererBuilder builder = new PdfRendererBuilder();

		builder.withHtmlContent(html, null);
		builder.toStream(outputStream);
		builder.run();

		// PDFレスポンスの生成
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + pdfFileName + ".pdf");

			return ResponseEntity.ok()
					.headers(headers)
					.contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(inputStream));
		}
	}

	/**
	 *(日報帳票) HTMLをPDF化
	 * @param userIdString
	 * @param targetYearMonth
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/output/dailyReportOutput/pdf")
	public ResponseEntity<InputStreamResource> generateDailyReportPdf(
			Model model, HttpSession session, @RequestParam("image") String imageData) throws IOException {

		// 固有のデータを取得
		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		MonthlyDailyReportDto monthlyDailyReport = (MonthlyDailyReportDto) session.getAttribute("monthlyDailyReport");

		// テンプレート用データをセットアップ
		Map<String, Object> variables = new HashMap<>();
		variables.put("monthlyDailyReport", monthlyDailyReport);
		variables.put("targetYearMonth", targetYearMonth);
		variables.put("userData", userData);
		variables.put("image", imageData);
		variables.put("useBootstrap", false); // Bootstrapを使用しない場合

		// 共通メソッドに渡す
		String templatePath = "dailyReportOutput";
		String pdfFileName = "dailyReportOutput_" + targetYearMonth;
		return generatePdfFile(templatePath, variables, pdfFileName);
	}

	/**
	 * (勤怠帳票) HTMLをPDF化
	 * @param model
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/output/attendanceOutput/pdf")
	public ResponseEntity<InputStreamResource> generateAttendancePdf(
			Model model, HttpSession session/*, @RequestParam("image") String imageData*/) throws IOException {

		// 固有のデータを取得
		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		Map<Byte, String> statusMap = (Map<Byte, String>) session.getAttribute("statusMap");
		MonthlyAttendanceDto monthlyAttendanceDto = (MonthlyAttendanceDto) session.getAttribute("monthlyAttendance");

		// テンプレート用データをセットアップ
		Map<String, Object> variables = new HashMap<>();
		variables.put("statusMap", statusMap);
		variables.put("monthlyAttendance", monthlyAttendanceDto);
		variables.put("targetYearMonth", targetYearMonth);
		variables.put("userData", userData);
		//		variables.put("image", imageData);
		variables.put("useBootstrap", false); // Bootstrapを使用しない場合

		// 共通メソッドに渡す
		String templatePath = "attendanceOutput";
		String pdfFileName = "attendanceOutput_" + targetYearMonth;
		return generatePdfFile(templatePath, variables, pdfFileName);
	}
	
	/**
	 * (勤怠日報帳票）HTMLをPDF化
	 * @param model
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/output/attendanceReportOutput/pdf")
	public ResponseEntity<InputStreamResource> generatePdf(Model model, HttpSession session) throws IOException {

		// 固有のデータを取得
		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		List<AttendanceReportDto> attendanceReportDtoList = (List<AttendanceReportDto>) session.getAttribute("attendanceReportDtoList");
		
		// テンプレート用データをセットアップ
		Map<String, Object> variables = new HashMap<>();
		variables.put("attendanceReportDtoList", attendanceReportDtoList);
		variables.put("userData", userData);
		variables.put("targetYearMonth", targetYearMonth);
		variables.put("useBootstrap", false);
		
		// 共通メソッドに渡す
		String templatePath = "attendanceReportOutput";
		String pdfFileName = "attendanceReportOutput_" + targetYearMonth;
		return generatePdfFile(templatePath, variables, pdfFileName);
	}

}