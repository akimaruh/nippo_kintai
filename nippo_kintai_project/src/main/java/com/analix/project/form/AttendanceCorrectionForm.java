package com.analix.project.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttendanceCorrectionForm {
	
	private Integer id;
	private Integer userId;
	private Byte status;
	
//	@NotNull(message = "日付は必須です。")
	private String date;
	
	@NotNull(message = "出勤は必須です。", groups = {RequestAttendanceCorrectionGroup.class})
	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "HH:mm形式で入力して下さaい。", groups = {RequestAttendanceCorrectionGroup.class})
	private String startTime;
	
	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "HH:mm形式で入力して下さい。", groups = {RequestAttendanceCorrectionGroup.class})
	private String endTime;
	
	@Size(max = 20, message = "備考は20字以内で入力して下さい。", groups = {RequestAttendanceCorrectionGroup.class})
	private String remarks;
	
	@NotBlank(message = "訂正理由は必須です。", groups = { RequestAttendanceCorrectionGroup.class})
	@Size(max = 20, message = "訂正理由は20字以内で入力して下さい。", groups = {RequestAttendanceCorrectionGroup.class})
	private String correctionReason;
	
	@NotBlank(message = "却下理由は必須です。", groups = { RejectAttendanceCorrectionGroup.class})
	@Size(max = 20, message = "却下理由は20字以内で入力して下さい。", groups = {RejectAttendanceCorrectionGroup.class})
	private String rejectionReason;
	
	private String applicationDate;
	
	private String confirmer;
	
	private Byte rejectFlg;
	
//	private String userName;

}
