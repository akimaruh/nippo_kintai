package com.analix.project.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class MonthlyAttendanceReqDto {
	
	/** 申請ID */
	private Integer id;
	/** 申請者のユーザーID */
	private Integer userId;
	/** 申請対象年月 */
	private Date targetYearMonth;
	/** 申請日 */
	private Date date;
	/** ステータス */
	private Integer status;
	/** 氏名 */
	private String name;
	/** メール */
	private String email;
	
	private String role;
	/** 年月 */
	private String yearMonth;
	
}
