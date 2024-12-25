package com.analix.project.util;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.analix.project.dto.AttendanceCommonDto;

@Component
public class AttendanceMessageUtil {

	/**
	 * 共通
	 * @param commonDto
	 * @return
	 */
	public static String formatText(AttendanceCommonDto commonDto) {
		String status = AttendanceUtil.getAttendanceStatus(commonDto.getStatus());
		LocalTime startTime = commonDto.getStartTime();
		LocalTime endTime = commonDto.getEndTime();
		String time = (startTime != null && endTime != null) ? startTime + "~" + endTime : " - ";
		String remarks = (commonDto.getRemarks() == null || commonDto.getRemarks().trim().isEmpty()) ? "なし"
				: commonDto.getRemarks();

		return String.format("[%s][%s][備考:%s]", status, time, remarks);

	}

	/**
	 * 訂正
	 * @param commonDto
	 * @param correctionReason
	 * @return
	 */
	public static String formatCorrectionMessage(AttendanceCommonDto commonDto, String correctionReason) {
		String correctedReason = (correctionReason == null || correctionReason.trim().isEmpty()) ? "なし"
				: correctionReason;

		String formattedText = formatText(commonDto);

		return "訂正：" + formattedText + "[訂正理由:" + correctedReason + "]";
	}

	/**
	 * 月次
	 * @param commonDto
	 * @return
	 */
	public static String formatAttendanceMessage(AttendanceCommonDto commonDto) {
		String formattedText = formatText(commonDto);
		
		return "勤怠：" + formattedText;
	}

}
