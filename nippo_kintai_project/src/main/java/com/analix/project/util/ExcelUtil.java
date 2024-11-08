package com.analix.project.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import com.analix.project.dto.WorkbookDto;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExcelUtil {
	/* 縦軸最大桁数 */
	public static final int MAX_COL_NUM = 16384;
	/* エクセル縦軸文字を数値に変換 */
	public static final String MAX_COL_STR = CellReference.convertNumToColString(ExcelUtil.MAX_COL_NUM - 1);
	/* ワークブック初期化 */
	private Workbook wb = null;

	/**
	 * Excelファイルのダウンロード
	 * @param workbookDto
	 * @param response
	 * @throws IOException
	 */
	public static void downloadBook(WorkbookDto workbookDto, HttpServletResponse response) throws IOException {

		workbookDto.setWbName("日報帳票202411_SYS5_秋丸葉玲.xlsx");
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
			//失敗メッセージを出せるように後々return=true;に変更
			e.printStackTrace();
		} finally {
			workbookDto.getWb().close();
		}
	}

	/**
	 * テーブルヘッダー行生成
	 * @param workbook
	 * @param headerArray
	 * @return テーブルヘッダー行反映後シート
	 */
	public Sheet setHeaderRow(Object[] headerArray,String sheetName) {

		// ヘッダーのスタイル
		CellStyle headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		Sheet sheet = wb.createSheet();
		Row headerRow = sheet.createRow(0);

		for (int i = 1; i < headerArray.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue((String) headerArray[i]);
			cell.setCellStyle(headerStyle);
		}
		return sheet;

	}
	public Sheet setBodyRow(Object[] bodyArray,String sheetName) {

		// データのスタイル
		CellStyle bodyStyle = wb.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);

		int rowIndex = 2;
		Sheet sheet = wb.getSheetAt(0);
		Row bodyRow = sheet.createRow(rowIndex++);
		for (int i = 1; i < bodyArray.length; i++) {
			Cell cell = bodyRow.createCell(i);
			cell.setCellValue((String) bodyArray[i]);
			cell.setCellStyle(bodyStyle);
		}
		return sheet;
	}

	public Sheet setBodyRow(Object[][] bodyArray,String sheetName) {

		// データのスタイル
		CellStyle bodyStyle = wb.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);

		int rowIndex = 2;
		Sheet sheet = wb.getSheetAt(0);
		for (Object[] rowData : bodyArray) {
			Row bodyRow = sheet.createRow(rowIndex++);
			for (int i = 0; i < rowData.length; i++) {
				Cell cell = bodyRow.createCell(i);
				if (rowData[i] instanceof String) {
					cell.setCellValue((String) rowData[i]);
				} else if (rowData[i] instanceof Integer) {
					cell.setCellValue((Integer) rowData[i]);
				}
				cell.setCellStyle(bodyStyle);
			}
		}
		return sheet;
	}

//	public static Sheet setAutoSizeColumn() {
//		// 列の幅を自動調整
//		for (int i = 0; i < headers.length; i++) {
//			sheet.autoSizeColumn(i);
//		}
//	}

}
