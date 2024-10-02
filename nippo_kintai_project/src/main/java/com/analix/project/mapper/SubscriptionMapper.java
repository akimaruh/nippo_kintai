package com.analix.project.mapper;

import java.util.List;

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
//	public Subscription findSubscriptionByUserId(@Param("userId") Integer userId);
//	
//	public SubscriptionDto findSubscriptionDtoByUserId(@Param("userId") Integer userId);
	
	public Subscriptions findSubscriptionsByUserId(@Param("userId") Integer userId);
	
	
	/**
	 * マネージャーのサブスクリプション情報を取得
	 * @return
	 */
	public List<Subscriptions> getManagerSubscriptions();
	
	
	/**
	 * サブスクリプション情報がデータベースに存在するか確認
	 * @param userId
	 * @param endpoint
	 * @return カウント数 0:存在しない 1以上:存在する
	 */
	public Integer existsByUserIdAndEndpoint(@Param("userId") Integer userId, @Param("endpoint") String endpoint);

}
