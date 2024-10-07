package com.analix.project.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.SubscriptionDto;
import com.analix.project.entity.Subscriptions;
import com.analix.project.entity.Users;
import com.analix.project.mapper.SubscriptionMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Subscription.Keys;

@Service
public class WebPushService {

	@Autowired
	private SubscriptionMapper subscriptionMapper;
	@Autowired
	private UserMapper userMapper;

	private static final String publicKey = "BKjsGZugSkRldTB5GjYYIrtLLmqyn-Ae56DQefHAxJYIqRERWspUFjLBdAyiciwg3TkDKz2kJsmQMeNRRpSaHOg "; // 公開鍵を設定
	private static final String privateKey = "bJ7wMCQFNOS7XCiAWoSlpBtgy_YINkT8yNyFVY95X-A"; // 秘密鍵を設定

	public void sendPushNotification(Subscription subscription, String payload)
			throws GeneralSecurityException, IOException, JoseException {
		// PushServiceを初期化
		PushService pushService = new PushService()
				.setPublicKey(publicKey)
				.setPrivateKey(privateKey);

		// Notificationを作成
		Notification notification = new Notification(subscription, payload);

		// プッシュ通知を送信
		try {
			pushService.send(notification);
			System.out.println("サービス：通知が正常に送信されました！");
		} catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
			System.out.println("通知送信中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * サブスクリプション情報をデータベースに保存
	 * @param subscriptionDto
	 */
	public void saveSubscription(SubscriptionDto subscriptionDto) {
		subscriptionMapper.insertSubscription(
				subscriptionDto.getUserId(), subscriptionDto.getEndpoint(),
				subscriptionDto.getKeys().getP256dh(), subscriptionDto.getKeys().getAuth());
	}

	// 承認ボタン押下時
	public void sendApprovePush(Integer userId, String payload)
			throws GeneralSecurityException, IOException, JoseException {
		// サブスクリプションの存在確認
		Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(userId);

		if (entity != null) {
			Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
			Subscription subscription = new Subscription(entity.getEndpoint(), keys);
			sendPushNotification(subscription, payload);
		} else {
			System.out.println("(承認)サブスクリプションが存在しません。" + userId);
		}
	}

	// 却下ボタン押下時
	public void sendRejectPush(Integer userId, String payload)
			throws GeneralSecurityException, IOException, JoseException {
		// サブスクリプションの存在確認
		Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(userId);

		if (entity != null) {
			Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
			Subscription subscription = new Subscription(entity.getEndpoint(), keys);
			sendPushNotification(subscription, payload);
		} else {
			System.out.println("(却下)サブスクリプションが存在しません。" + userId);
		}

	}

	// 承認申請ボタン押下時
	public void sendRequestPush(String payload)
			throws GeneralSecurityException, IOException, JoseException {
		List<Users> managerList = userMapper.findUserListByRole(Constants.CODE_VAL_MANAGER);

		for (Users manager : managerList) {
			// サブスクリプションの存在確認
			Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(manager.getId());

			if (entity != null) {
				Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
				Subscription subscription = new Subscription(entity.getEndpoint(), keys);
				sendPushNotification(subscription, payload);
			} else {
				System.out.println("(申請)サブスクリプションが存在しません。" + manager.getId());
			}
		}
	}

}