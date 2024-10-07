package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.analix.project.dto.SubscriptionDto;
import com.analix.project.entity.Users;
import com.analix.project.service.WebPushService;

import jakarta.servlet.http.HttpSession;

@RestController
public class PushNotificationController {

    @Autowired
    private WebPushService webPushService;
    
    /**
     * サブスクリプション情報を取得後データベースに保存
     * @param subscriptionDto
     * @return 成功時HTTPステータスに200
     */
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody SubscriptionDto subscriptionDto, HttpSession session) {
    	// サブスクリプション情報を保存する処理
    	Users user = (Users) session.getAttribute("loginUser");
    	Integer userId = user.getId();
        subscriptionDto.setUserId(userId); // SubscriptionDtoにuserIdをセット
    	
    	webPushService.saveSubscription(subscriptionDto);
    	return ResponseEntity.ok().build(); // 成功時には200を返す
    }
    
    // 通知を送りたいユーザー(マネージャー)のサブスクリプション情報がデータベースに存在するか確認
//    @PostMapping("/checkSubscription")
//    public ResponseEntity<Boolean> checkSubscription(@RequestBody Subscriptions subscriptions){
//    	Integer userId = subscriptions.getUserId();
//    	String endpoint = subscriptions.getEndpoint();
//    	
//    	boolean exists = webPushService.checkSubscriptionExists(userId, endpoint);
//    	return ResponseEntity.ok(exists);
//    }
    
//	@PostMapping("/send")
//	public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
//		// 送信するメッセージを受け取り、プッシュ通知を送信
//		try {
//			String message = request.getMessage();
//			Subscription subscription = request.getSubscription();
//
//			webPushService.sendPushNotification(subscription, message);
//			return ResponseEntity.ok("Notification sent successfully!");
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("Error sending notification: " + e.getMessage());
//		}
//
//	}
}


//    private List<Subscription> subscriptions = new ArrayList<>();
//    private final String privateKey = "bJ7wMCQFNOS7XCiAWoSlpBtgy_YINkT8yNyFVY95X-A"; // 秘密鍵
//    private final String publicKey = "BKjsGZugSkRldTB5GjYYIrtLLmqyn-Ae56DQefHAxJYIqRERWspUFjLBdAyiciwg3TkDKz2kJsmQMeNRRpSaHOg"; // 公開鍵
//    
//    @PostMapping("/subscribe")
//    public ResponseEntity<String> subscribe(@RequestBody Subscription subscription) {
//        // サブスクリプション情報を保存し、通知を送信する例
//    	try {
//    	webPushService.sendPushNotification(subscription, "メッセージ");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // エラー時には500を返す
//        }
//        return new ResponseEntity<>(HttpStatus.OK); // 成功時には200を返す
//    }
//    
//    @PostMapping("/subscribe")
//    public void subscribe(@RequestBody PushSubscription subscription) {
//        Subscription.Keys keys = new Subscription.Keys(subscription.getKeys().getP256dh(), subscription.getKeys().getAuth());
//        Subscription newSubscription = new Subscription(subscription.getEndpoint(), keys);
//        subscriptions.add(newSubscription);
//        System.out.println("新しい購読者が追加されました: " + subscription);
//        System.out.println("newSubscription: " + newSubscription);
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
//    	System.out.println("send入った");
//        try {
//            PushService pushService = new PushService();
//            pushService.setPublicKey(publicKey);
//            pushService.setPrivateKey(privateKey);
//            
//            System.out.println("send1");
//
//            // 最初の購読者に通知を送信
//            if (!subscriptions.isEmpty()) {
//            	System.out.println("send2");
//            	Subscription subscription = subscriptions.get(0);
//                String endpoint = subscription.endpoint;
//                String userPublicKey = subscription.keys.p256dh;
//                String userAuth = subscription.keys.auth;
//                //byte[] payload = request.getMessage().getBytes(StandardCharsets.UTF_8);
//                
//             // JSON形式でペイロードを作成
//                String message = request.getMessage();
//                String jsonPayload = String.format("{\"title\":\"%s\", \"message\":\"%s\"}", "テスト通知", message);
//                byte[] payload = jsonPayload.getBytes(StandardCharsets.UTF_8);
//                
//                // デフォルトのTTLを定義
//                int ttl = 28 * 86400; // 28日
//
//
//                // Notificationのインスタンスを作成
//                Notification notification = new Notification(endpoint, userPublicKey, userAuth, payload, ttl);
//                System.out.println("send3");
//                pushService.send(notification);
//            }
//            
//            System.out.println("通知送信: " + request);
//            String jsonResponse = "{\"title\": \"テスト通知\", \"message\": \"プッシュ通知が送信されました。。。\"}";
//            System.out.println("Sending response: " + jsonResponse);
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body("{\"title\": \"テスト通知\", \"message\": \"プッシュ通知が送信されました。。。\"}");
//
//
//            
//        } catch (Exception e) {
//            System.out.println("通知送信エラー: " + e.getMessage());
//            return ResponseEntity.status(500)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body("{\"error\": \"通知送信エラー\", \"message\": \"" + e.getMessage() + "\"}");
//        }
//    }
//}

