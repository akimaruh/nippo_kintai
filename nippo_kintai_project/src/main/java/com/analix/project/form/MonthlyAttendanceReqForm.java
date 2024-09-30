package com.analix.project.form;

import java.time.LocalDate;

import lombok.Data;
@Data
public class MonthlyAttendanceReqForm {
	/** 申請ID */
	private Integer id;
	/** 申請者のユーザーID */
	private Integer userId;
	/** 申請対象年月 */
	private LocalDate targetYearMonth;
	/** 申請日 */
	private LocalDate date;
	/** ステータス */
	private Integer status;
	/** 氏名 */
	private String name;
	/** メール */
	private String email;
	
	private String role;

}
