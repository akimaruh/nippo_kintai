package com.analix.project.dto;

import java.time.YearMonth;

import org.apache.poi.ss.usermodel.Workbook;

import com.analix.project.entity.Users;

import lombok.Data;

@Data
public class WorkbookDto {
	/** ワークブック */
	private Workbook wb;
	/** ワークブック名 */
	private String wbName;
	/*ユーザー情報*/
	private Users userData;
	/*対象年月*/
	private YearMonth targetYearMonth;

}
