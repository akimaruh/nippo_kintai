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
import com.analix.project.mapper.SubscriptionMapper;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Subscription.Keys;

@Service
public class WebPushService {
	
	@Autowired
	private SubscriptionMapper subscriptionMapper; 
	
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
			System.out.println("通知が正常に送信されました！");
		} catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
			System.out.println("通知送信中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    
    
    /**
     * サブスクリプション情報をデータベースに保存
     * @param subscriprtionDto
     */
    public void saveSubscription(SubscriptionDto subscriprtionDto) {
    	subscriptionMapper.insertSubscription(
    			subscriprtionDto.getUserId(), subscriprtionDto.getEndpoint(), 
    			subscriprtionDto.getKeys().getP256dh(), subscriprtionDto.getKeys().getAuth());
    }
    
	// 承認ボタン押下時
	public void sendApprovePush(Integer userId)
			throws GeneralSecurityException, IOException, JoseException {
		String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"承認されました\"}";
		Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(userId);
		Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
		Subscription subscription = new Subscription(entity.getEndpoint(), keys);
		sendPushNotification(subscription, payload);
	}
	
	// 却下ボタン押下時
	public void sendRejectPush(Integer userId)
			throws GeneralSecurityException, IOException, JoseException {
		String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"承認されました\"}";
		Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(userId);
		Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
		Subscription subscription = new Subscription(entity.getEndpoint(), keys);
		sendPushNotification(subscription, payload);
	}

	// 承認申請ボタン押下時
	public void sendRequestPush(String payload)
			throws GeneralSecurityException, IOException, JoseException {
		List<Subscriptions> managerList = subscriptionMapper.getManagerSubscriptions();
		for (Subscriptions manager : managerList) {
//			String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"申請があります\"}";
			System.out.println("ペイロード: " + payload);
			Keys keys = new Keys(manager.getP256dh(), manager.getAuth());
			Subscription subscription = new Subscription(manager.getEndpoint(), keys);
			System.out.println("マネージャーだれ？" + manager.getUserId());
			sendPushNotification(subscription, payload);
		}
	}
		
	// テストボタン押下時
	public void sendTestPush(Integer userId)
			throws GeneralSecurityException, IOException, JoseException {
		String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"テストです。\"}";
		Subscriptions entity = subscriptionMapper.findSubscriptionsByUserId(userId);
		Keys keys = new Keys(entity.getP256dh(), entity.getAuth());
		Subscription subscription = new Subscription(entity.getEndpoint(), keys);
		sendPushNotification(subscription, payload);
	}	
}

//    public void addSubscription(Subscription request) {
//        subscriptions.add(request);
//    }

//    public void sendPushNotification(String title, String body) {
//        String payload = String.format("{\"title\":\"%s\", \"body\":\"%s\"}", title, body);
//
//        for (Subscription subscription : subscriptions) {
//            String endpoint = subscription.endpoint;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            // VAPIDの公開鍵をAuthorizationヘッダーに追加
//            headers.set("Authorization", "vapid t=" + VAPID_PUBLIC_KEY + ", k=" + VAPID_PRIVATE_KEY);
//
//            HttpEntity<String> request = new HttpEntity<>(payload, headers);
//
//            // プッシュ通知を送信
//            restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
//        }
//    }
    
//    public void sendPushNotification(Subscription subscription, String message) {
//    	PushService pushService = new PushService() 
//    		.setPublicKey(publicKey)
//			.setPrivateKey(privateKey);
//    	Notification notification = new Notification(subscription, message);
//    	
//    	pushService.send(notification);
//    	
//    }

