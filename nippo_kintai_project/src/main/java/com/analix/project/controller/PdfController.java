package com.analix.project.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

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

import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.service.DailyReportService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Controller
public class PdfController {
	@Autowired
	private SpringTemplateEngine templateEngine;
	@Autowired
	private DailyReportService dailyReportService;

	//	@GetMapping("/output/dailyReportOutput")
	//	public void getPdfOutputData(@RequestParam("userId") String userIdString,
	//			@RequestParam("targetYearMonth") YearMonth targetYearMonth, Model model) {
	//		Integer userId = Integer.parseInt(userIdString);
	//		//ユーザーIDと年月から1か月の個人日報リストを取得
	//		List<DailyReportDto> dailyReportListByUserIdANDYearMonth = dailyReportService
	//				.getDailyReportListForOutput(userId, targetYearMonth);
	//		//生成対象のHTMLのディレクトリを作成
	//		String htmldirectory = "dailyReportOutput";
	//
	//		//生成したPDFの名前を作成
	//		String pdfFileName = "dailyReportOutput" + targetYearMonth.getYear() + "-" + targetYearMonth.getMonthValue();
	//
	//		try {
	//			generatePdf(model, dailyReportListByUserIdANDYearMonth, htmldirectory, pdfFileName, targetYearMonth);
	//		} catch (IOException e) {
	//			System.out.println("出力データ取得失敗");
	//			e.printStackTrace();
	//		}
	//
	//	}
	//
	//	public ResponseEntity<InputStreamResource> generatePdf(Model model, List<?> generateList, String htmldirectory,
	//			String pdfFileName, YearMonth targetYearMonth)
	//			throws IOException {
	//		// テンプレートエンジンにデータを渡す
	//		Context context = new Context();
	//		System.out.println("generateList:" + generateList);
	//		context.setVariable("generateList", generateList);
	//		context.setVariable("targetYearMonth", targetYearMonth);
	//
	//		// HTMLを生成
	//		String html = templateEngine.process("output/" + htmldirectory, context);
	//
	//		// HTMLをPDFに変換
	//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	//		PdfRendererBuilder builder = new PdfRendererBuilder();
	//		builder.withHtmlContent(html, "base-url"); // base-urlが必要な場合
	//		//		builder.withHtmlContent(html, null);
	//		builder.toStream(outputStream);
	//		builder.run();
	//
	//		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
	//			HttpHeaders headers = new HttpHeaders();
	//			headers.add("Content-Disposition", "inline; filename= " + pdfFileName + ".pdf");
	//
	//			return ResponseEntity.ok()
	//					.headers(headers)
	//					.contentType(MediaType.APPLICATION_PDF)
	//					.body(new InputStreamResource(inputStream));
	//		}
	//
	//	}
	/**
	 * HTMLをPDF化
	 * @param userIdString
	 * @param targetYearMonth
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/output/dailyReportOutput/pdf")
	public ResponseEntity<InputStreamResource> generatePdf(
			@RequestParam("userId") String userIdString,
			@RequestParam("userName") String userName,
			@RequestParam("targetYearMonth") YearMonth targetYearMonth, Model model,
			@RequestParam("image") String imageData)
			throws IOException {
		System.out.println("generatePdf通過");
		// テンプレートエンジンにデータを渡す
		Context context = new Context();
		Integer userId = Integer.parseInt(userIdString);

		//		// Base64データからプレフィックスを削除
//		String base64Image = imageData.split(",")[1];
		//
		//		// デコードして利用する処理を書く
		//		byte[] imageBytes = Base64.getDecoder().decode(base64Image);
		//		 String base64Image = imageData.split(",")[1];
		//		    byte[] imageBytes = Base64.getDecoder().decode(imageData.split(",")[1]);

		// Base64にエンコード
		//		    String base64ImageForHtml = Base64.getEncoder().encodeToString(imageBytes);
//		String imageHtml = "data:image/png;base64," + base64Image;
		//LocalDate now = LocalDate.now();
		//YearMonth yearMonth= YearMonth.of(now.getYear(), now.getMonthValue());
		List<DailyReportDto> dailyReportListByUserIdANDYearMonth = dailyReportService
				.getDailyReportListForOutput(userId, targetYearMonth);
		DailyReportSummaryDto dailyReportSummaryDto = dailyReportService.getMonthlyDailyReportSummary(userId,
				targetYearMonth);
		//System.out.println("generateList:"+generateList);
		//		System.out.println("timePerDayMapList:"+timePerDayMapList);
		context.setVariable("generateList", dailyReportListByUserIdANDYearMonth);
		context.setVariable("dailyReportSummary", dailyReportSummaryDto);
		context.setVariable("targetYearMonth", targetYearMonth);
		context.setVariable("userId", userId);
		context.setVariable("userName", userName);
		context.setVariable("image", imageData);
		context.setVariable("useBootstrap", false); // Bootstrapを使用しない場合

		//生成対象のHTMLのディレクトリを作成
		String htmldirectory = "dailyReportOutput";

		//生成したPDFの名前を作成
		String pdfFileName = "dailyReportOutput2024-10";
		// HTMLを生成
		String html = templateEngine.process("output/" + htmldirectory, context);

		// HTMLをPDFに変換
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfRendererBuilder builder = new PdfRendererBuilder();

		builder.withHtmlContent(html, null);
		builder.toStream(outputStream);
		builder.run();

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename= " + pdfFileName + ".pdf");

			return ResponseEntity.ok()
					.headers(headers)
					.contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(inputStream));
		}

	}

}
