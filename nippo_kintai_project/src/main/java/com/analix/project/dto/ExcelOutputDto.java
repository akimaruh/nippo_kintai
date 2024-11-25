package com.analix.project.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ExcelOutputDto {

	/** 日報ID */
	private Integer reportId;
	/*ユーザID*/
	private Integer userId;
	/*ユーザ名*/
	private String userName;
	/*日付*/
	private LocalDate date;
	/*日報詳細*/
	private List<ExcelOutputDetailDto> excelOutputDetailDtoList;
	
	/*1日の作業時間*/
	private Integer timePerDay;
	/** 項目番号 */
	private Integer fieldNum;
	/** 項目名 */
	private String fieldName;
	/** 日付出力行 */
	private Integer row;
	/** 日付出力列 */
	private Integer clm;
	/** 必須フラグ */
	private Byte requiredFlg;
	/** 型 */
	private Byte inputType;
	/** 範囲From */
	private Integer rangeFrom;
	/** 範囲To */
	private Integer rangeTo;
	/** 内容 */
	private String content;
}
