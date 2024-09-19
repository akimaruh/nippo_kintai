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
}
