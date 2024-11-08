package com.analix.project.dto;

import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;
@Data
public class WorkbookDto {
	/** ワークブック */
	private Workbook wb;
	/** ワークブック名 */
	private String wbName;


}
