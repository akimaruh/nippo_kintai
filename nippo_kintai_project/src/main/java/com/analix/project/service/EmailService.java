package com.analix.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private DailyReportService dailyReportService;

	public void sendEmail(String to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		try {
			mailSender.send(message);
			System.out.println("メール送信成功");
		} catch (Exception e) {
			System.out.println("メール送信失敗: " + e.getMessage());
		}
	}

	//	public void sendForgetRegistEmails(Map<String, List<Users>> umsubmitMap) {
	public void sendForgetRegistEmails() {
		//日報未提出者リストと、勤怠未提出者リストにそれぞれ分ける
		//		List<Users> umsubmittedDailyReportUserList = umsubmitMap.get("dailyReport");
		//		List<Users> umsubmittedAttendanceUserList = umsubmitMap.get("attendance");
		List<Users> umsubmittedDailyReportUserList = dailyReportService.registCheck();
		List<Users> umsubmittedAttendanceUserList = attendanceService.registCheck();
		System.out.println(umsubmittedDailyReportUserList);
		System.out.println(umsubmittedAttendanceUserList);
		//		// 勤怠未提出者リストをセットに変換
		//		Set<Users> unsubmittedAttendanceUsers = new HashSet<>(umsubmittedAttendanceUserList);
		//
		//		// 日報未提出者リストをセットに変換
		//		Set<Users> unsubmittedDailyReportUsers = new HashSet<>(umsubmittedDailyReportUserList);

		//メールを送るグループを作成
		//勤怠未提出者リスト
		List<Users> unsubmittedAttendanceOnlyList = getUnsubmittedAttendanceOnly(umsubmittedAttendanceUserList,
				umsubmittedDailyReportUserList);
		//日報未提出者リスト
		List<Users> unsubmittedDailyReportOnlyList = getUnsubmittedDailyReportOnly(umsubmittedAttendanceUserList,
				umsubmittedDailyReportUserList);
		//両方未提出者リスト
		List<Users> unsubmittedBothList = getUnsubmittedBoth(umsubmittedAttendanceUserList,
				umsubmittedDailyReportUserList);

		//マネージャーに未提出者一覧を送信

		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);
		for (Users manager : managerList) {
			String managerMessage = "日報未提出者一覧:" + umsubmittedDailyReportUserList.stream()
					.map(Users::getName).collect(Collectors.joining(","))
					+ Constants.LINE_SEPARATOR +
					"勤怠未提出者一覧:" + umsubmittedAttendanceUserList.stream()
							.map(Users::getName).collect(Collectors.joining(","));

			sendEmail(manager.getEmail(), "未提出者一覧", managerMessage);
		}

		//未提出者に通知を送信

		if (unsubmittedAttendanceOnlyList != null) {
			//勤怠未提出者に通知を送信
			for (Users unsubmittedAttendanceOnly : unsubmittedAttendanceOnlyList) {
				String userMessage = "本日の勤怠が未提出です。早急に提出してください。";
				sendEmail(unsubmittedAttendanceOnly.getEmail(), "勤怠未提出通知", userMessage);
			}
		}
		if (unsubmittedDailyReportOnlyList != null) {
			//日報未提出者に通知を送信
			for (Users unsubmittedDailyReportOnly : unsubmittedDailyReportOnlyList) {
				String userMessage = "本日の日報が未提出です。早急に提出してください。";
				sendEmail(unsubmittedDailyReportOnly.getEmail(), "日報未提出通知", userMessage);
			}
		}
		if (unsubmittedBothList != null) {
			//両方未提出者に通知を送信
			for (Users unsubmittedBoth : unsubmittedBothList) {
				String userMessage = "本日の日報・勤怠が未提出です。早急に提出してください。";
				sendEmail(unsubmittedBoth.getEmail(), "日報・勤怠未提出通知", userMessage);
			}
		}
		System.out.println("バッチメール送信処理終了");

	}

	/**
	 * 勤怠のみ未提出者抽出
	 * @param unsubmittedAttendanceUsers
	 * @param unsubmittedDailyReportUsers
	 * @return 勤怠のみ未提出者リスト
	 */
	public List<Users> getUnsubmittedAttendanceOnly(List<Users> umsubmittedAttendanceUserList,
			List<Users> umsubmittedDailyReportUserList) {
		// 勤怠未提出者リストをセットに変換
		Set<Users> unsubmittedAttendanceUsers = new HashSet<>(umsubmittedAttendanceUserList);
		// 日報未提出者リストをセットに変換
		Set<Users> unsubmittedDailyReportUsers = new HashSet<>(umsubmittedDailyReportUserList);
		System.out.println("Set<Users>" + unsubmittedAttendanceUsers);
		System.out.println("Set<Users>" + unsubmittedDailyReportUsers);

		// 勤怠未提出者リストから、日報未提出者を除外
		unsubmittedAttendanceUsers.removeAll(unsubmittedDailyReportUsers);
		System.out.println("勤怠のみ未提出者抽出" + new ArrayList<>(unsubmittedAttendanceUsers));
		return new ArrayList<>(unsubmittedAttendanceUsers);
	}

	/**
	 * 日報のみ未提出者抽出
	 * @param unsubmittedAttendanceUsers
	 * @param unsubmittedDailyReportUsers
	 * @return 日報のみ未提出者リスト
	 */
	public List<Users> getUnsubmittedDailyReportOnly(List<Users> umsubmittedAttendanceUserList,
			List<Users> umsubmittedDailyReportUserList) {
		// 勤怠未提出者リストをセットに変換
		Set<Users> unsubmittedAttendanceUsers = new HashSet<>(umsubmittedAttendanceUserList);
		// 日報未提出者リストをセットに変換
		Set<Users> unsubmittedDailyReportUsers = new HashSet<>(umsubmittedDailyReportUserList);
		System.out.println("Set<Users>" + unsubmittedAttendanceUsers);
		System.out.println("Set<Users>" + unsubmittedDailyReportUsers);

		// 日報未提出者リストから、勤怠未提出者を除外
		unsubmittedDailyReportUsers.removeAll(unsubmittedAttendanceUsers);
		System.out.println("日報のみ未提出者抽出" + new ArrayList<>(unsubmittedDailyReportUsers));
		return new ArrayList<>(unsubmittedDailyReportUsers);
	}

	/**
	 * 勤怠日報未提出者抽出
	 * @param unsubmittedAttendanceUsers
	 * @param unsubmittedDailyReportUsers
	 * @return 勤怠日報未提出者リスト
	 */
	public List<Users> getUnsubmittedBoth(List<Users> umsubmittedAttendanceUserList,
			List<Users> umsubmittedDailyReportUserList) {
		// 勤怠未提出者リストをセットに変換
		Set<Users> unsubmittedAttendanceUsers = new HashSet<>(umsubmittedAttendanceUserList);
		// 日報未提出者リストをセットに変換
		Set<Users> unsubmittedDailyReportUsers = new HashSet<>(umsubmittedDailyReportUserList);
		System.out.println("Set<Users>" + unsubmittedAttendanceUsers);
		System.out.println("Set<Users>" + unsubmittedDailyReportUsers);
		// ユーザーIDで共通の未提出者を取得
		unsubmittedAttendanceUsers.retainAll(unsubmittedDailyReportUsers);
		System.out.println("勤怠日報未提出者抽出" + new ArrayList<>(unsubmittedAttendanceUsers));
		return new ArrayList<>(unsubmittedAttendanceUsers);
	}

	
	// 勤怠承認申請
	public void sendRequestEmail(MonthlyAttendanceReqDto request) {
		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);
		for (Users manager : managerList) {
			String subject = "【日報勤怠アプリ】勤怠承認申請のお知らせ";
			String content = request.getName() + "さんの" + request.getTargetYearMonth() + "の承認申請があります。";
			System.out.println("Service managerEmail: " + manager.getEmail() +  manager.getName());
			sendEmail(manager.getEmail(), subject, content);
		}
	}

	// 承認
	public void sendApproveEmail(MonthlyAttendanceReqDto request) {
		String subject = "【日報勤怠アプリ】承認のお知らせ";
		String content = request.getTargetYearMonth() + "の承認申請が承認されました。";
		sendEmail(request.getEmail(), subject, content);
	}

	// 却下
	public void sendRejectEmail(MonthlyAttendanceReqDto request) {
		String subject = "【日報勤怠アプリ】却下のお知らせ";
		String content = request.getTargetYearMonth() + "の承認申請が却下されました。";
		sendEmail(request.getEmail(), subject, content);
	}

	//	@PostConstruct
	//	public void init() {
	//		sendTestEmail("");
	//	}
	//	
	//	public void sendTestEmail(String to) {
	//		sendEmail(to, "テストメール", "テストメールです。");
	//	}

}
