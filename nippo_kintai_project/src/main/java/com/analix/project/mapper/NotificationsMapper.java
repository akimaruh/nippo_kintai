package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.NotificationsDto;
import com.analix.project.entity.Notifications;

@Mapper
public interface NotificationsMapper {
	/**
	 * 通知登録
	 * @param notificationDto
	 * @return 反映結果
	 */
	boolean insertNotifications(@Param("notification") Notifications notifications);

	Integer getLastInsertId();
	
	List<NotificationsDto> getNotification(@Param("userId") Integer userId);
}
