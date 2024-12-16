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
import com.analix.project.entity.Users;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.servlet.http.HttpSession;

//@RequiredArgsConstructor
@Controller
public class PdfController {
	@Autowired
	private SpringTemplateEngine templateEngine;

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
			Model model, HttpSession session,
			@RequestParam("image") String imageData)
			throws IOException {

		//前ページで取得した内容をセッションから再取得
		Users userData = (Users) session.getAttribute("userData");
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("targetYearMonth");
		@SuppressWarnings("unchecked")
		List<DailyReportDto> generateList = (List<DailyReportDto>) session.getAttribute("dailyReportDtoList");
		DailyReportSummaryDto dailyReportSummaryDto = (DailyReportSummaryDto) session
				.getAttribute("dailyReportSummaryDto");

		// テンプレートエンジンにデータを渡す
		Context context = new Context();
		context.setVariable("generateList", generateList);
		context.setVariable("dailyReportSummary", dailyReportSummaryDto);
		context.setVariable("targetYearMonth", targetYearMonth);
		context.setVariable("userData", userData);
		context.setVariable("image", imageData);
		context.setVariable("useBootstrap", false); // Bootstrapを使用しない場合

		//生成対象のHTMLのディレクトリを作成
		String htmldirectory = "dailyReportOutput";

		//生成したPDFの名前を作成
		String pdfFileName = "dailyReportOutput_" + targetYearMonth;
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
