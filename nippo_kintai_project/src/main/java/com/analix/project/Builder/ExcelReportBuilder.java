package com.analix.project.Builder;

import java.time.YearMonth;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.analix.project.entity.Users;
import com.analix.project.util.ExcelUtil;

import lombok.Data;

@Data
public class ExcelReportBuilder {
	private Workbook workbook;
	private Sheet sheet;

	public ExcelReportBuilder(String sheetName) {
		this.workbook = new XSSFWorkbook();
		this.sheet = workbook.createSheet(sheetName);
	}

	public ExcelReportBuilder setHeaderRow(Users userData, YearMonth targetYearMonth) {
		ExcelUtil.setHeaderRow(workbook, userData, targetYearMonth, sheet.getSheetName());
		return this;
	}

	public ExcelReportBuilder addRows(Object[] headers, Object[][] data) {
		ExcelUtil.setRow(headers, data, workbook, sheet.getSheetName());
		return this;
	}

	public ExcelReportBuilder addRows(Object[] headers, Object[] data) {
		ExcelUtil.setRow(headers, data, workbook, sheet.getSheetName());
		return this;
	}

	public ExcelReportBuilder addDecorations() {
		ExcelUtil.setDefaultStyle(workbook);
		return this;
	}

	public Workbook build() {
		return workbook;
	}
}