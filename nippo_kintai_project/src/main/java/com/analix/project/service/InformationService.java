package com.analix.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;

import jakarta.servlet.http.HttpSession;

@Service
public class InformationService {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 日報・勤怠忘れメッセージセット
	 * @param session
	 * @return
	 */
	public String forgetInfo(HttpSession session) {
		Users user = (Users) session.getAttribute("loginUser");

		List<Users> umsubmittedDailyReportUserList = dailyReportService.registCheck();
		List<Users> umsubmittedAttendanceUserList = attendanceService.registCheck();

		//メッセージの送り方グループを作成
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
		if (user.getRole() == Constants.CODE_VAL_MANAGER) {
			String managerMessage = "日報未提出者一覧:" + umsubmittedDailyReportUserList.stream()
					.map(Users::getName).collect(Collectors.joining(","))
					+ Constants.LINE_SEPARATOR +
					"勤怠未提出者一覧:" + umsubmittedAttendanceUserList.stream()
							.map(Users::getName).collect(Collectors.joining(","));
			return managerMessage;
		}

		//未提出者に通知を送信
		if (user.getRole() == Constants.CODE_VAL_REGULAR || user.getRole() == Constants.CODE_VAL_UNITMANAGER) {
			if (unsubmittedAttendanceOnlyList != null) {
				//勤怠未提出者に通知を送信

				String userMessage = "本日の勤怠が未提出です。早急に提出してください。";
				return userMessage;

			}
		}
		if (unsubmittedDailyReportOnlyList != null) {
			//日報未提出者に通知を送信

			String userMessage = "本日の日報が未提出です。早急に提出してください。";
			return userMessage;

		}

		if (unsubmittedBothList != null) {
			//両方未提出者に通知を送信

			String userMessage = "本日の日報・勤怠が未提出です。早急に提出してください。";
			return userMessage;

		}
		return null;
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

}
