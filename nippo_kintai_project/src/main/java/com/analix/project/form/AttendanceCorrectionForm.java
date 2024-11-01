package com.analix.project.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttendanceCorrectionForm {
	
	private Integer id;
	
	private Integer userId;
	
	private Byte status;
	
//	@NotNull(message = "日付は必須です。")
	private String date;
	
//	@NotNull(message = "出勤は必須です。")
//	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "HH:mm形式で入力して下さaい。")
	private String startTime;
	
//	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "HH:mm形式で入力して下さい。")
	private String endTime;
	
//	@Size(max = 20, message = "備考は20字以内で入力して下さい。")
	private String remarks;
	
//	@Size(max = 20, message = "訂正理由は10字以内で入力して下さい。")
	private String correctionReason;
	
	@NotNull(message = "却下理由は必須です。")
	@Size(max = 20, message = "却下理由は10字以内で入力して下さい。")
	private String rejectionReason;
	
	private String applicationDate;
	
	private String confirmer;
	
	private Byte rejectFlg;

}
