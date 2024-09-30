// ServiceWorkerはクライアント側で通知を受信するために必要

self.addEventListener('push', function(event) {
	console.log('プッシュ通知を受信しました');
	console.log('一番上のデータ、タイトル:' + data, title);
	
	const data = event.data ? event.data.json() : { title: 'デフォルトタイトル', body: 'デフォルトメッセージ' };
    const title = data.title || '通知';
    const options = {
        body: data.body || '通知が届きました',
        icon: '/img/pet_hoso_cat.png'
    };
	
	// 受信した通知をブラウザに表示
	event.waitUntil(
        self.registration.showNotification(title, options) // 受信した通知をブラウザに表示する部分
    );
    
})


// 承認ボタン押下時
document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('approve-btn').addEventListener('click', function() {

		// サーバーからデータを取得
		fetch('/attendance/update', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ buttonType: 'approve' })
		})
			.then(response => response.json())
			.then(data => {
				const title = data.title || '承認';  // サーバーからのタイトルを使う
				const body = data.body || '承認されました';  // サーバーからの本文を使う
				const icon = '/img/pet_hoso_cat.png';

				const showNotification = () => new Notification(title, { body, icon });

				if (Notification.permission === 'granted') {
					showNotification();
				} else {
					Notification.requestPermission().then(permission => {
						if (permission === 'granted') showNotification();
					});
				}
			})
			.catch(error => console.error('Error:', error));
	});
});


// 却下ボタン押下時
document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('reject-btn').addEventListener('click', function() {

		// サーバーからデータを取得
		fetch('/attendance/update', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ buttonType: 'reject' })
		})
			.then(response => response.json())
			.then(data => {
				const title = data.title || '【日報勤怠アプリ】承認申請結果';  // サーバーからのタイトルを使う
				const body = data.body || '申請が却下されました。再度申請を行ってください。';  // サーバーからの本文を使う
				const icon = '/img/pet_hoso_cat.png';

				const showNotification = () => new Notification(title, { body, icon });

				if (Notification.permission === 'granted') {
					showNotification();
				} else {
					Notification.requestPermission().then(permission => {
						if (permission === 'granted') showNotification();
					});
				}
			})
			.catch(error => console.error('Error:', error));
	});
});

// 承認申請ボタン押下時
document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('request-btn').addEventListener('click', function() {
		console.log('承認申請');
		
		// サーバーからデータを取得
		fetch('/attendance/approveRequestComplete', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ buttonType: 'request' })
		})
			.then(response => response.json())
			.then(data => {
				console.log('承認申請のデータ、タイトル:' + data.title, data.body);
				const title = data.title || '【日報勤怠アプリ】勤怠承認申請';
				const body = data.body || '承認申請があります。';
				const icon = '/img/pet_hoso_cat.png';

				const showNotification = () => new Notification(title, { body, icon });

				if (Notification.permission === 'granted') {
					showNotification();
				} else {
					Notification.requestPermission().then(permission => {
						if (permission === 'granted') showNotification();
					});
				}
			})
			.catch(error => console.error('Error:', error));
	});
});

// テストボタン押下時
//document.addEventListener('DOMContentLoaded', function() {
//    document.getElementById('test-btn').addEventListener('click', function(event) {
//        event.preventDefault(); // フォームのデフォルト動作を防ぐ
//
//        // サーバーからデータを取得
//        fetch('/attendance/test', {
//            method: 'POST',
//            headers: { 'Content-Type': 'application/json' },
//        })
//        .then(response => {
//            if (!response.ok) {
//                throw new Error('Network response was not ok');
//            }
//            return response.json();
//        })
//        .then(data => {
//			console.log('あ' + data.title);
//            const title = data.title || 'テスト通知';
//            const body = data.body || 'テスト通知が送信されました';
//            const icon = '/img/pet_hoso_cat.png';
//
//            const showNotification = () => new Notification(title, { body, icon });
//
//            if (Notification.permission === 'granted') {
//                showNotification();
//            } else {
//                Notification.requestPermission().then(permission => {
//                    if (permission === 'granted') showNotification();
//                });
//            }
//        })
//        .catch(error => console.error('Error:', error));
//    });
//});

// テスト２ボタン押下時
document.addEventListener('DOMContentLoaded', function() {
	document.getElementById('test-btn').addEventListener('click', function() {

		// サーバーからデータを取得
		fetch('/attendance/test', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ buttonType: 'reject' })
		})
			.then(response => response.json())
			.then(data => {
				console.log('２' + data.title);
				const title = data.title || 'テスト通知';
           		const body = data.message || 'テスト通知が送信されました';
            	const icon = '/img/pet_hoso_cat.png';

				const showNotification = () => new Notification(title, { body, icon });

				if (Notification.permission === 'granted') {
					showNotification();
				} else {
					Notification.requestPermission().then(permission => {
						if (permission === 'granted') showNotification();
					});
				}
			})
			.catch(error => console.error('Error:', error));
	});
});






//self.addEventListener('push', function(event) {　// プッシュ通知を受信したときに呼び出される
//    let data;
//
//    try {
//        data = event.data ? event.data.json() : { title: 'テスト通知', message: 'swプッシュ通知が送信されました！' };
//        console.log('Received push data:', data); // データをログに出力
//    } catch (error) {
//		console.log('event.data', event.data);
//		console.log('event.data.json()',event.data.json());
//        console.error('Invalid JSON in push data:', error);
//        return;
//    }
//
//    const options = {
//        body: data.message
////        body: data.message,
////        icon: 'icon.png',
////        badge: 'badge.png'
//    };
//
//    event.waitUntil(
//        self.registration.showNotification(title, options) // 受信した通知をブラウザに表示する部分
//    );
//});
	

//self.addEventListener('notificationclick', function(event) {
//    event.notification.close(); // 通知を閉じる
//
//    event.waitUntil(
//        clients.matchAll({ type: 'window' }).then(clients => {
//            // 通知がクリックされたときに、特定のウィンドウを開く
//            const client = clients.find(client => client.url === '/' && 'focus' in client);
//            if (client) {
//                return client.focus();
//            } else {
//                return clients.openWindow('/'); // ルートに戻る
//            }
//        })
//    );
//});


