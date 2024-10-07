// 通知の権限を確認し、許可が得られたらService Workerを登録
Notification.requestPermission().then(function(permission) { // 現在の許可状態を確認
    if (permission === 'granted') {
        // ユーザーが通知を許可した場合の処理
        console.log('通知の許可が得られました。');
 
        // ブラウザがService Workerをサポートしているか確認し、/sw.jsを登録
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/pushApi/sw.js')
                .then(function(registration) {
                    console.log('Service Workerが正常に登録されました:', registration);
                    return registration.pushManager.getSubscription().then(function(subscription) {
                        if (subscription === null) { // サブスクリプションが存在しない場合は作成する
                            const vapidPublicKey = 'BKjsGZugSkRldTB5GjYYIrtLLmqyn-Ae56DQefHAxJYIqRERWspUFjLBdAyiciwg3TkDKz2kJsmQMeNRRpSaHOg';
                            const convertedVapidKey = urlBase64ToUint8Array(vapidPublicKey); // Base64形式をUint8Arrayに変換
                            return registration.pushManager.subscribe({ // 新しいサブスクリプションを作成
                                userVisibleOnly: true,
                                applicationServerKey: convertedVapidKey
                            }).then(function(subscription) { // サブスクリプションが成功したらその情報をコンソールに表示
                                console.log('サブスクリプションに成功しました！', subscription);
                                sendSubscriptionToServer(subscription);
                            });
                        } else {
                            console.log('既にサブスクリプションされています:', subscription);
                        }
                    });
                }).catch(function(error) {
                    console.error('Service Workerの登録に失敗しました:', error);
                });
        }
    } else {
        console.warn('通知の許可が得られませんでした:', permission);
    }
});
 
// ユーザーのサブスクリプション情報を取得してサーバーに送信
function sendSubscriptionToServer(subscription) {
    fetch('/subscribe', {
        method: 'POST',
        body: JSON.stringify({
            endpoint: subscription.endpoint,
            keys: {
                p256dh: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('p256dh')))), // Base64エンコード
                auth: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('auth')))) // Base64エンコード
            }
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(function(response) {
        if (response.ok) {
            console.log('サーバーにサブスクリプションが送信されました');
        } else {
            return response.text().then(text => {
                console.error('サブスクリプションの送信に失敗しました:', text);
            });
        }
    }).catch(function(error) {
        console.error('サブスクリプションの送信中にエラーが発生しました:', error);
    });
};
 
// Base64の変換
function urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding).replace(/\-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
};

	//	ボタンごと
//	document.getElementById('approve-btn').addEventListener('click', function(event) {
//    event.preventDefault(); // デフォルトのフォーム送信を防ぐ
//
//    fetch('/attendance/update', {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/json'
//        },
//        body: JSON.stringify({ buttonType: 'approve' }) // ボタンタイプを設定
//    }).then(response => {
//        if (response.ok) {
//            console.log('サーバーからの受信データ:', response.text()); // 受信データの表示
//        } else {
//            throw new Error('承認リクエストの送信に失敗しました。');
//        }
//    }).catch(error => {
//        console.error('エラー:', error);
//    });
//});




		
		


//	document.getElementById('rejectButton').addEventListener('click', function() {
//		fetch('/approve', {
//			method: 'POST',
//			headers: {
//				'Content-Type': 'application/json'
//			},
//			body: JSON.stringify({ buttonType: 'reject', userId: 'USER_ID' }) // 必要に応じてユーザーIDを設定
//		});
//	});

// 通知の権限を確認し、許可が得られたらService Workerを登録
//Notification.requestPermission().then(function(permission) {
//    switch (permission) {
//        case 'granted':
//            // ユーザーが通知を許可した場合の処理
//            console.log('通知の許可が得られました。');
//
//            // ブラウザがService Workerをサポートしているか確認し、/sw.jsを登録
//            if ('serviceWorker' in navigator) {
//                navigator.serviceWorker.register('/pushApi/sw.js')
//                .then(function(registration) {
//                    console.log('Service Workerが正常に登録されました:', registration);
//                    return registration.pushManager.getSubscription().then(function(subscription) {
//                        if (subscription === null) { // サブスクリプションが存在しない場合は作成する
//                            const vapidPublicKey = 'BKjsGZugSkRldTB5GjYYIrtLLmqyn-Ae56DQefHAxJYIqRERWspUFjLBdAyiciwg3TkDKz2kJsmQMeNRRpSaHOg';
//                            const convertedVapidKey = urlBase64ToUint8Array(vapidPublicKey); // Base64形式をUint8Arrayに変換
//                            return registration.pushManager.subscribe({ // 新しいサブスクリプションを作成
//                                userVisibleOnly: true,
//                                applicationServerKey: convertedVapidKey
//                            }).then(function(subscription) { // サブスクリプションが成功したらその情報をコンソールに表示
//                                console.log('サブスクリプションに成功しました！', subscription);
//                                sendSubscriptionToServer(subscription);
//                            });
//                        } else {
//                            console.log('既にサブスクリプションされています:', subscription);
//                        }
//                    });
//                }).catch(function(error) {
//                    console.error('Service Workerの登録に失敗しました:', error);
//                });
//            }
//            break;
//
//        case 'denied':
//            // ユーザーが通知をブロックした場合の処理
//            console.warn('通知がブロックされました。');
//            break;
//
//        case 'default':
//            // ユーザーが通知のリクエストを無視した場合の処理
//            console.log('通知のリクエストが無視されました。');
//            break;
//
//        default:
//            console.log('予期しない権限の状態:', permission);
//            break;
//    }
//});
