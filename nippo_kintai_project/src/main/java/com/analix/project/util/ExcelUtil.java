package com.analix.project.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.time.YearMonth;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import com.analix.project.dto.WorkbookDto;
import com.analix.project.entity.Users;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExcelUtil {
	/* 縦軸最大桁数 */
	public static final int MAX_COL_NUM = 16384;
	/* エクセル縦軸文字を数値に変換 */
	public static final String MAX_COL_STR = CellReference.convertNumToColString(ExcelUtil.MAX_COL_NUM - 1);
	/* ワークブック初期化 */
	//	private static Workbook wb = null;
	static int rowIndex = 0;

	/**
	 * Excelワークブックの作成
	 * @param outputName
	 * @param targetName
	 * @param loginUser
	 * @return ワークブック名
	 * @throws IOException 
	 */
	//	public void createWorkbook(String outputName, YearMonth targetYearMonth, Users userData, WorkbookDto workbookDto,
	//			HttpServletResponse response) throws IOException {
	//		Workbook wb = new XSSFWorkbook();
	//		Sheet sheet = wb.createSheet(outputName);
	//		String targetYearMonthForFile = targetYearMonth.toString().replaceAll("-", "");
	//		String wbName = outputName + targetYearMonthForFile + "_" + userData.getDepartmentName() + "_"
	//				+ userData.getName()
	//				+ ".xlsx";
	//		workbookDto.setWbName(wbName);
	//		workbookDto.setWb(wb);
	//		ExcelUtil.downloadBook(workbookDto, response);
	//	}

	/**
	 * Excelファイルのダウンロード
	 * @param workbookDto
	 * @param response
	 * @throws IOException
	 */
	public static void downloadBook(WorkbookDto workbookDto, HttpServletResponse response) throws IOException {

		String targetYearMonthForFile = workbookDto.getTargetYearMonth().toString().replaceAll("-", "");
		String wbName = workbookDto.getWbName() + targetYearMonthForFile + "_"
				+ workbookDto.getUserData().getDepartmentName() + "_"
				+ workbookDto.getUserData().getName()
				+ ".xlsx";
		workbookDto.setWbName(wbName);

		//ワークブック名を基にファイル名をセット
		String fileNameSjis = new String(workbookDto.getWbName().getBytes("Shift_JIS"), "ISO-8859-1").replace(" ",
				"%20");
		String fileNameUtf8 = URLEncoder.encode(workbookDto.getWbName(), "UTF-8").replace("+", "%20");
		//HTTPヘッダに、ダウンロードファイル名を設定
		response.addHeader("Content-Disposition",
				"attachment; filename=" + fileNameSjis + ";filename*=utf-8''" + fileNameUtf8);

		//Excelファイルの作成と、レスポンスストリームへの書き込み
		try (ServletOutputStream stream = response.getOutputStream()) {
			// Excel出力
			workbookDto.getWb().write(stream);

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			rowIndex = 0;
			workbookDto.getWb().close();
		}
	}
	
	/**
	 * CSVファイルのダウンロード
	 * @param fileName
	 * @param headers
	 * @param response
	 * @throws IOException
	 */
	public static void downloadCSV(String fileName, String[] headers, HttpServletResponse response) throws IOException {
		String fileNameCsv = fileName + ".csv";

		// ファイル名をShift-JISとUTF-8でエンコード
		String fileNameSjis = new String(fileNameCsv.getBytes("Shift_JIS"), "ISO-8859-1").replace(" ", "%20");
		String fileNameUtf8 = URLEncoder.encode(fileNameCsv, "UTF-8").replace("+", "%20");

		//レスポンスのヘッダー設定
		response.setContentType("text/csv");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Content-Disposition",
				"attachment; filename=" + fileNameSjis + ";filename*=utf-8''" + fileNameUtf8);

		//CSV書き込み処理
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"))) {
			writer.write("\uFEFF");
			writer.write(String.join(",", headers));
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ヘッダー行生成
	 * @param workbook
	 * @param userData
	 * @param targetYearMonth
	 * @param sheetName
	 */
	public static void setHeaderRow(Workbook workbook, Users userData, YearMonth targetYearMonth,
			String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		Row headerRow = sheet.createRow(rowIndex);
		String[] labels = { "社員コード：", "ユーザー名：", "対象年月：" };
		String[] values = {
				userData.getEmployeeCode().toString(), // ユーザーID
				userData.getName(), // ユーザー名
				targetYearMonth.toString() // 対象年月
		};
		
		// 左寄せのスタイル設定
		CellStyle leftAlignStyle = workbook.createCellStyle();
		leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
		
		//社員コードを数値にするため先に設定
		headerRow = sheet.createRow(rowIndex++);
		headerRow.createCell(0).setCellValue(labels[0]);
		headerRow.createCell(1).setCellValue(userData.getEmployeeCode());
		headerRow.getCell(1).setCellStyle(leftAlignStyle);
		
		for (int i = 1; i < labels.length; i++) {
			headerRow = sheet.createRow(rowIndex++);
			headerRow.createCell(0).setCellValue(labels[i]);
			headerRow.createCell(1).setCellValue(values[i]);
		}
		
		// 空白行を追加
		sheet.createRow(rowIndex++);
	}
	
	/**
	 * テーブルヘッダーのスタイル設定
	 * @param workbook
	 * @return
	 */
	public static CellStyle setHeaderStyle(Workbook workbook) {
		// ヘッダーのスタイル
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		
		// 背景色の設定
		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return headerStyle;
	}

	/**
	 * エクセルデフォルトのスタイル設定
	 * @param workbook
	 * @return
	 */
	public static CellStyle setDefaultStyle(Workbook workbook) {
		// データのスタイル
		CellStyle bodyStyle = workbook.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);
		
		// 背景色の設定
		bodyStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		bodyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return bodyStyle;
	}

	/**
	 * テーブルボディ行生成
	 * @param Object[]型bodyArray 
	 * @param workbook
	 */
	public static void setRow(Object[] headerArray, Object[] bodyArray, Workbook workbook, String sheetName) {

		Sheet sheet = workbook.getSheet(sheetName);		
		Row headerRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < headerArray.length; i++) {
			Cell cell = headerRow.createCell(i);
			if (headerArray[i] instanceof String) {
				cell.setCellValue((String) headerArray[i]);
			} else if (headerArray[i] instanceof Integer) {
				cell.setCellValue((Integer) headerArray[i]);
			}
			cell.setCellStyle(setHeaderStyle(workbook));
		}
		Row bodyRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < bodyArray.length; i++) {
			Cell cell = bodyRow.createCell(i);
			if (bodyArray[i] instanceof String) {
				cell.setCellValue((String) bodyArray[i]);
			} else if (bodyArray[i] instanceof Integer) {
				cell.setCellValue((Integer) bodyArray[i]);
			}
			cell.setCellStyle(setDefaultStyle(workbook));

		}
		rowIndex++;

	}

	/**
	 * テーブルボディ行生成 
	 * @param Object[][]型bodyArray
	 * @param workbook
	 */
	public static void setRow(Object[] headerArray, Object[][] bodyArray, Workbook workbook, String sheetName) {
		System.out.println("bodyArray:" + Arrays.deepToString(bodyArray));
		Sheet sheet = workbook.getSheet(sheetName);
		
		// 列の幅
		int columnWidth = 11 * 256;
		
		Row headerRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < headerArray.length; i++) {
			Cell cell = headerRow.createCell(i);
			if (headerArray[i] instanceof String) {
				cell.setCellValue((String) headerArray[i]);
			} else if (headerArray[i] instanceof Integer) {
				cell.setCellValue((Integer) headerArray[i]);
			}
			cell.setCellStyle(setHeaderStyle(workbook));
			sheet.setColumnWidth(i, columnWidth);
		}
		for (Object[] rowData : bodyArray) {
			System.out.println("rowData:" + Arrays.toString(rowData));
			Row bodyRow = sheet.createRow(rowIndex++);
			for (int i = 0; i < rowData.length; i++) {
				Cell cell = bodyRow.createCell(i);
				if (rowData[i] instanceof String) {
					cell.setCellValue((String) rowData[i]);
				} else if (rowData[i] instanceof Integer) {
					cell.setCellValue((Integer) rowData[i]);
				}
				cell.setCellStyle(setDefaultStyle(workbook));

			}
		}
		rowIndex++;

	}

}
