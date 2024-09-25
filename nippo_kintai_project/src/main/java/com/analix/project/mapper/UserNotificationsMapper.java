package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.UserNotification;

@Mapper
public interface UserNotificationsMapper {
	/**
	 * ユーザー通知登録
	 * @param notificationDto
	 * @return 反映結果
	 */
	boolean insertUserNotifications(@Param("userNotificationsList") List<UserNotification> userNotificationsList);
	/**
	 * ユーザー通知紐づけ確認
	 * @param notificationId
	 * @param userId
	 * @return
	 */
	UserNotification findByNotificationIdAndUserId(@Param("notificationId")Integer notificationId,@Param("userId")Integer userId);
	
	
	/**
	 * 既読更新
	 * @param userNotification
	 * @return
	 */
	boolean readNotification(@Param("userNotification") UserNotification userNotification);
}
