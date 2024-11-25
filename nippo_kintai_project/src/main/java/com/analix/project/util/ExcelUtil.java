package com.analix.project.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.YearMonth;

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
	private Workbook wb = null;
	static int rowIndex = 0;

	/**
	 * Excelワークブック名の作成
	 * @param outputName
	 * @param targetName
	 * @param loginUser
	 * @return ワークブック名
	 */
	public String createWorkbookName(String outputName, YearMonth targetYearMonth, Users userData) {
		String targetYearMonthForFile = targetYearMonth.toString().replaceAll("-", "");
		String wbName = outputName + targetYearMonthForFile + "_" + userData.getDepartmentName() + "_"
				+ userData.getName()
				+ ".xlsx";
		return wbName;
	}

	/**
	 * Excelファイルのダウンロード
	 * @param workbookDto
	 * @param response
	 * @throws IOException
	 */
	public static void downloadBook(WorkbookDto workbookDto, HttpServletResponse response) throws IOException {

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

			//			return true;
		} catch (Exception e) {
			//失敗メッセージを出せるように後々return=true;に変更
			e.printStackTrace();
			//			return false;
		} finally {
			rowIndex = 0;
			workbookDto.getWb().close();
		}
	}

	/**
	 * ヘッダー行生成
	 * @param workbook
	 * @param userData
	 * @param targetYearMonth
	 * @param sheetName
	 */
	public void setHeaderRow(Workbook workbook, Users userData, YearMonth targetYearMonth,
			String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		Row headerRow;
		String[] labels = { "ユーザーID：", "ユーザー名：", "対象年月：" };
		String[] values = {
				userData.getId().toString(), // ユーザーID
				userData.getName(), // ユーザー名
				targetYearMonth.toString() // 対象年月
		};
		for (int i = 0; i < labels.length; i++) {
			headerRow = sheet.createRow(rowIndex++);
			headerRow.createCell(0).setCellValue(labels[i]);
			headerRow.createCell(1).setCellValue(values[i]);
		}

	}

	public CellStyle setDefaultStyle(Workbook workbook) {
		// データのスタイル
		CellStyle bodyStyle = workbook.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);
		return bodyStyle;
	}

	/**
	 * テーブルボディ行生成
	 * @param Object[]型bodyArray 
	 * @param workbook
	 */
	public void setRow(Object[] headerArray, Object[] bodyArray, Workbook workbook, String sheetName) {

		Sheet sheet = workbook.getSheet(sheetName);
		Row headerRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < headerArray.length; i++) {
			Cell cell = headerRow.createCell(i);
			if (headerArray[i] instanceof String) {
				cell.setCellValue((String) headerArray[i]);
			} else if (headerArray[i] instanceof Integer) {
				cell.setCellValue((Integer) headerArray[i]);
			}
			cell.setCellStyle(setDefaultStyle(workbook));
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
	public void setRow(Object[] headerArray, Object[][] bodyArray, Workbook workbook, String sheetName) {

		Sheet sheet = workbook.getSheet(sheetName);
		Row headerRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < headerArray.length; i++) {
			Cell cell = headerRow.createCell(i);
			if (headerArray[i] instanceof String) {
				cell.setCellValue((String) headerArray[i]);
			} else if (headerArray[i] instanceof Integer) {
				cell.setCellValue((Integer) headerArray[i]);
			}
			cell.setCellStyle(setDefaultStyle(workbook));
		}
		for (Object[] rowData : bodyArray) {
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
