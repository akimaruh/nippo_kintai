package com.analix.project.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.MessageUtil;

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
	@Autowired
	private MessageUtil messageUtil;
	
	
	
	/*
	 * 
	 */
	@Async
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
	/**
	 * 日報勤怠忘れメール送信
	 * @param umsubmitMap
	 */
	public void sendForgetRegistEmails(Map<String, List<Users>> umsubmitMap) {
		//日報未提出者リストと、勤怠未提出者リストにそれぞれ分ける
		List<Users> umsubmittedDailyReportUserList = umsubmitMap.get("dailyReport");
		List<Users> umsubmittedAttendanceUserList = umsubmitMap.get("attendance");
		
		System.out.println(umsubmittedDailyReportUserList);
		System.out.println(umsubmittedAttendanceUserList);

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
			String managerMessage = "本日の日報・勤怠未提出者" + Constants.LINE_SEPARATOR + "日報未提出者:"
					+ umsubmittedDailyReportUserList.stream()
							.map(Users::getName).collect(Collectors.joining(","))
					+ Constants.LINE_SEPARATOR +
					"勤怠未提出者:" + umsubmittedAttendanceUserList.stream()
							.map(Users::getName).collect(Collectors.joining(","))+ messageUtil.mailCommonMessageForBatch();

			sendEmail(manager.getEmail(), "【日報勤怠アプリ】日報・勤怠未提出者一覧", managerMessage);
		}

		//未提出者に通知を送信

		if (unsubmittedAttendanceOnlyList != null) {
			//勤怠未提出者に通知を送信
			for (Users unsubmittedAttendanceOnly : unsubmittedAttendanceOnlyList) {
				String userMessage = "本日の勤怠が未提出です。早急に提出してください。"+ messageUtil.mailCommonMessageForBatch();
				sendEmail(unsubmittedAttendanceOnly.getEmail(), "【日報勤怠アプリ】勤怠未提出", userMessage);
			}
		}
		if (unsubmittedDailyReportOnlyList != null) {
			//日報未提出者に通知を送信
			for (Users unsubmittedDailyReportOnly : unsubmittedDailyReportOnlyList) {
				String userMessage = "本日の日報が未提出です。早急に提出してください。" + messageUtil.mailCommonMessageForBatch();
				sendEmail(unsubmittedDailyReportOnly.getEmail(), "【日報勤怠アプリ】日報未提出", userMessage);
			}
		}
		if (unsubmittedBothList != null) {
			//両方未提出者に通知を送信
			for (Users unsubmittedBoth : unsubmittedBothList) {
				String userMessage = "本日の日報・勤怠が未提出です。早急に提出してください。" + messageUtil.mailCommonMessageForBatch();
				sendEmail(unsubmittedBoth.getEmail(), "【日報勤怠アプリ】日報・勤怠未提出", userMessage);
			}
		}
		System.out.println("バッチメール送信処理終了");

	}
	
	/**
	 * 各ボタン押下時のメール送信
	 */
	/**
	 * 勤怠月次申請提出時
	 * @param request
	 */
	@Async
	public void sendRequestEmail(MonthlyAttendanceReqDto request, String mailMessage) {
		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);
		for (Users manager : managerList) {
			int year = request.getTargetYearMonth().getYear();
			int month = request.getTargetYearMonth().getMonthValue();
			YearMonth targetYearMonth = YearMonth.of(year, month);
			String subject = "【日報勤怠アプリ】勤怠月次申請";
			String content = request.getName() + "さんの" + targetYearMonth + "の月次申請があります。\n" + mailMessage;
			System.out.println("Service managerEmail: " + manager.getEmail() + manager.getName());
			sendEmail(manager.getEmail(), subject, content);
		}
	}

	/**
	 * 月次申請承認時
	 * @param userId
	 * @param targetYearMonth
	 * @param mailMessage
	 */
	@Async
	public void sendApproveEmail(Integer userId,YearMonth targetYearMonth, String mailMessage) {
		
		String subject = "【日報勤怠アプリ】承認";
		String email =userMapper.findEmailByUserId(userId);
//		int year = request.getTargetYearMonth().getYear();
//		int month = request.getTargetYearMonth().getMonthValue();
//		YearMonth targetYearMonth = YearMonth.of(year, month);
		String content = targetYearMonth + "の月次申請が承認されました。\n" + mailMessage;
		sendEmail(email, subject, content);
	}

	/**
	 * 月次申請却下時
	 * @param userId
	 * @param targetYearMonth
	 * @param mailMessage
	 */
	@Async
	public void sendRejectEmail(Integer userId,YearMonth targetYearMonth, String mailMessage) {
		String subject = "【日報勤怠アプリ】却下";
		String email =userMapper.findEmailByUserId(userId);
//		int year = request.getTargetYearMonth().getYear();
//		int month = request.getTargetYearMonth().getMonthValue();
//		YearMonth targetYearMonth = YearMonth.of(year, month);
		String content = targetYearMonth + "の月次申請が却下されました。\n" + mailMessage;
		sendEmail(email, subject, content);
	}
	

	/**
	 * 勤怠訂正申請提出時
	 * @param userName
	 * @param correctionDate
	 * @param mailMessage
	 */
	@Async
	public void sendCorrectionRequestEmail(String userName, String correctionDate, String mailMessage) {
		String formattedCorrectionDate = correctionDate.replace("-", "/"); // yyyy/MM
		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);
		for (Users manager : managerList) {
			String subject = "【日報勤怠アプリ】勤怠訂正申請";
			String content = userName + "さんの" + formattedCorrectionDate + "の訂正申請があります。\n" + mailMessage;
			sendEmail(manager.getEmail(), subject, content);
		}
	}

	 /**
	  * 訂正申請承認時
	  * @param userId
	  * @param formattedDate
	  * @param mailMessage
	  */
	@Async
	public void sendCorrectionApproveEmail(Integer userId, String formattedDate, String mailMessage) {
		String subject = "【日報勤怠アプリ】承認";
		String email =userMapper.findEmailByUserId(userId);
		String content = formattedDate + "の訂正申請が承認されました。\n" + mailMessage;
		sendEmail(email, subject, content);
	}
	
	/**
	 * 訂正申請却下時
	 * @param userId
	 * @param formattedDate
	 * @param mailMessage
	 */
	@Async
	public void sendCorrectionRejectEmail(Integer userId, String formattedDate, String mailMessage) {
		String subject = "【日報勤怠アプリ】却下";
		String email =userMapper.findEmailByUserId(userId);
		String content = formattedDate + "の訂正申請が却下されました。\n" + mailMessage;
		sendEmail(email, subject, content);
	}

	/**
	 * システム障害発生時メール送信
	 * @param ex
	 */
	@Async
	public void sendErrorNotification(Exception ex, List<Users> users) {

		for (Users admin : users) {
			String to = admin.getEmail(); // 管理者のメールアドレス
			String subject = "【日報勤怠アプリ】システム障害発生";
			String content = "以下のエラーが発生しました:\n\n" + ex.getMessage();
			sendEmail(to, subject, content);
		}

	}
	
	
	/**
	 * バッチ処理時のメール送信者抽出
	 */
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

	//	@PostConstruct
	//	public void init() {
	//		sendTestEmail("");
	//	}
	//	
	//	public void sendTestEmail(String to) {
	//		sendEmail(to, "テストメール", "テストメールです。");
	//	}

}
