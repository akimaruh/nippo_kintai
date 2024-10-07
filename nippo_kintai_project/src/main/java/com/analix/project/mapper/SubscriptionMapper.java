package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Subscriptions;

@Mapper
public interface SubscriptionMapper {
	
	/**
	 * サブスクリプション情報登録
	 * @param userId
	 * @param endpoint
	 * @param p256dh
	 * @param auth
	 */
	public void insertSubscription(@Param("userId") Integer userId, @Param("endpoint") String endpoint,
			@Param("p256dh") String p256dh, @Param("auth") String auth);
	
	
	/**
	 * ユーザーIDに基づいて最新のサブスクリプション情報を取得
	 * @param userId
	 * @return
	 */
	public Subscriptions findSubscriptionsByUserId(@Param("userId") Integer userId);

}
