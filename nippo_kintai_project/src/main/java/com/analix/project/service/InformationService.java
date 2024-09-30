package com.analix.project.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analix.project.dto.NotificationsDto;
import com.analix.project.entity.Notifications;
import com.analix.project.entity.UserNotification;
import com.analix.project.entity.Users;
import com.analix.project.mapper.NotificationsMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.mapper.UserNotificationsMapper;
import com.analix.project.util.Constants;

@Service
public class InformationService {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private NotificationsMapper notificationsMapper;
	@Autowired
	private UserNotificationsMapper userNotificationsMapper;

	/**
	 * 処理メニューのお知らせ表示
	 * @param userId
	 * @return
	 */
	public List<NotificationsDto> findNotification(Integer userId) {

		List<NotificationsDto> notificationsDtoList = notificationsMapper.getNotification(userId);
		for (NotificationsDto notificationsDto : notificationsDtoList) {
			String title = notificationsDto.getTitle();
			String message = notificationsDto.getMessage();
			LocalDate createDate = notificationsDto.getCreatedAt();

			String createMessage = "【" + title + "】" + message + "(" + createDate + ")";

			notificationsDto.setMessage(createMessage);

		}
		System.out.println(notificationsDtoList);
		return notificationsDtoList;
	}

	/**
	 * 通知テーブルエンティティ作成
	 * @param title
	 * @param message
	 * @param type
	 * @param date
	 * @return
	 */
	private Notifications createNotification(String title, String message, String type) {
		Notifications notification = new Notifications();
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setNotificationType(type);
		//notification.setCreatedAt(date);
		notificationsMapper.insertNotifications(notification);
		return notification;
	}

	/**
	 * バッチ用ユーザー通知エンティティ作成
	 * @param userList
	 * @param notificationId
	 */
	private void insertUserNotificationsForBatch(List<Users> userList, Integer notificationId) {
		List<UserNotification> userNotificationsList = userList.stream().map(user -> {
			UserNotification userNotification = new UserNotification();
			userNotification.setStatus("未読");
			userNotification.setUserId(user.getId());
			userNotification.setNotificationId(notificationId);
			return userNotification;
		}).collect(Collectors.toList());

		userNotificationsMapper.insertUserNotifications(userNotificationsList);
	}

	/**
	 * ユーザー通知エンティティ作成
	 * @param userId
	 * @param notificationId
	 */
	private void insertUserNotifications(Integer userId, Integer notificationId) {
		List<UserNotification> userNotificationsList = new ArrayList<>();
		UserNotification userNotification = new UserNotification();
		userNotification.setStatus("未読");
		userNotification.setUserId(userId);
		userNotification.setNotificationId(notificationId);
		userNotificationsList.add(userNotification);

		userNotificationsMapper.insertUserNotifications(userNotificationsList);
	}

	/**
	 * 承認申請通知作成
	 * @param userId
	 * @return
	 */
	public String approveRequestInsertNotifications(String name,YearMonth approveYearMonth) {
		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);
		// 承認申請通知作成
		Notifications approveRequestNotifications = createNotification("承認申請",
				name+"さんの"+approveYearMonth+"の承認申請があります。",
				"承認申請提出");
		Integer approveRequestNotificationId = notificationsMapper.getLastInsertId();
		for (Users manager : managerList) {
			insertUserNotifications(manager.getId(), approveRequestNotificationId);
		}

		return "登録完了";
	}

	/**
	 * 承認通知作成
	 * @param userId
	 * @return
	 */
	public String approveInsertNotifications(Integer userId,YearMonth targetYearMonth) {
		// 承認通知作成
		Notifications approveNotifications = createNotification("承認",
				targetYearMonth+"の承認申請が承認されました。",
				"承認");
		Integer approveNotificationId = notificationsMapper.getLastInsertId();
		insertUserNotifications(userId, approveNotificationId);
		return "登録完了";
	}

	/**
	 * 却下通知作成
	 * @param userId
	 * @return
	 */
	public String rejectInsertNotifications(Integer userId,YearMonth targetYearMonth) {
		// 却下通知作成
		Notifications rejectNotifications = createNotification("却下",
				targetYearMonth+"の承認申請が却下されました。",
				"却下");
		Integer rejectNotificationId = notificationsMapper.getLastInsertId();
		insertUserNotifications(userId, rejectNotificationId);
		return "登録完了";
	}

	public String insertErrorNotifications(Exception ex, List<Users> users) {
		String errorMessage = "以下のエラーが発生しました:\n\n" + ex.getMessage();
		Notifications errorNotifications = createNotification("システム障害発生", errorMessage, "システム障害");
		Integer errorNotificationId = notificationsMapper.getLastInsertId();
		for (Users admin : users) {
			insertUserNotifications(admin.getId(), errorNotificationId);
		}
		return "登録完了";

	}

	/**
	 * バッチ用日報勤怠忘れ通知
	 * @param unsubmittedDailyReportUserList
	 * @param unsubmittedAttendanceUserList
	 * @return
	 */
	public String insertNotificationsForBatch(List<Users> unsubmittedDailyReportUserList,
			List<Users> unsubmittedAttendanceUserList) {
		// 日報通知作成
		Notifications dailyReportNotifications = createNotification("日報未提出",
				"本日の日報が未提出です。提出してください。",
				"日報未提出");
		Integer dailyReportNotificationId = notificationsMapper.getLastInsertId();
		insertUserNotificationsForBatch(unsubmittedDailyReportUserList, dailyReportNotificationId);

		// 勤怠通知作成
		Notifications attendanceNotifications = createNotification("勤怠未提出",
				"本日の勤怠が未提出です。登録してください。",
				"勤怠未提出");
		Integer attendanceNotificationId = notificationsMapper.getLastInsertId();
		insertUserNotificationsForBatch(unsubmittedAttendanceUserList, attendanceNotificationId);

		// マネージャー通知作成
		String managerMessage = createManagerMessage(unsubmittedDailyReportUserList, unsubmittedAttendanceUserList);
		Notifications managerNotifications = createNotification("日報勤怠未提出者一覧",
				managerMessage,
				"日報勤怠未提出");
		Integer managerNotificationId = notificationsMapper.getLastInsertId();
		insertUserNotificationsForBatch(userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER),
				managerNotificationId);

		return "登録成功";
	}

	/**
	 * マネージャ用日報勤怠忘れメッセージ作成
	 * @param dailyReportUserList
	 * @param attendanceUserList
	 * @return
	 */
	private String createManagerMessage(List<Users> dailyReportUserList, List<Users> attendanceUserList) {
		return "日報未提出者一覧:" + dailyReportUserList.stream()
				.map(Users::getName)
				.collect(Collectors.joining(","))
				+ Constants.LINE_SEPARATOR +
				"勤怠未提出者一覧:" + attendanceUserList.stream()
						.map(Users::getName)
						.collect(Collectors.joining(","));
	}

	/**
	 * お知らせ通知リンク押下後の既読処理
	 * @param notificationId
	 * @param userId
	 * @return
	 */
	@Transactional
	public boolean markAsRead(Integer notificationId, Integer userId) {
		System.out.println("サービス入り");
		System.out.println("notificationID:" + notificationId + "userId" + userId);
		UserNotification userNotification = userNotificationsMapper.findByNotificationIdAndUserId(notificationId,
				userId);
		System.out.println(userNotification);
		if (userNotification == null) {
			System.out.println("通知ない");

		}

		userNotification.setStatus("既読"); // 既読状態にする
		LocalDate now = LocalDate.now();
		userNotification.setReadAt(now);
		return userNotificationsMapper.readNotification(userNotification); // データベースに更新
	}

}
