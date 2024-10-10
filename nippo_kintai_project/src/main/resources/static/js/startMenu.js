
function markAsRead(event, notificationId, userId, redirectUrl) {

	// デフォルトのリンク動作（GETリクエスト）を防ぐ
	event.preventDefault();
	// Ajaxリクエストを非同期で送信
	fetch('/notifications/markAsRead', {

		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			"notificationId": notificationId,
			"userId": userId
		}),


	})

		.then(response => {
			if (!response.ok) {
				throw new Error('既読処理に失敗しました');
			}
			console.log('既読処理が成功しました');
			window.location.href = redirectUrl;
		})
		.catch(error => {
			console.error('エラー:', error);

		});
}

function markAsReadForModal(event, notificationId, userId, index) {
	event.preventDefault(); // デフォルトのリンク遷移を防止

	// 通知の内容を取得（必要に応じてサーバーから取得する処理に変更）

	const message = document.getElementById("notificationsList[" + index + "].message").innerHTML;
	console.log(message);


	// モーダルのメッセージを設定
	document.getElementById('modalMessage').textContent = message;

	// モーダルを表示
	var myModal = new bootstrap.Modal(document.getElementById('myModal'));
	myModal.show();

	fetch('/notifications/markAsRead', {

		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			"notificationId": notificationId,
			"userId": userId
		}),


	})

		.then(response => {
			if (!response.ok) {
				throw new Error('既読処理に失敗しました');
			}
			console.log('既読処理が成功しました');
			window.location.href = '#';
		})
		.catch(error => {
			console.error('エラー:', error);

		});

}
//現在時刻を表示
function twoDigit(num) {
    return num < 10 ? "0" + num : num;
}

function showClock() {
    let nowTime = new Date();
    let nowHour = twoDigit(nowTime.getHours());
    let nowMin = twoDigit(nowTime.getMinutes());
    let msg = "現在時刻：" + nowHour + ":" + nowMin;
    document.getElementById("realtime").innerHTML = msg;
}
// 初期表示を行う
showClock();
// 1秒ごとに更新
setInterval(showClock, 1000);

////現在時刻を表示
//function twoDigit(num) {
//	let ret;
//	if (num < 10)
//		ret = "0" + num;
//	else
//		ret = num;
//	return ret;
//}
//function showClock() {
//	let nowTime = new Date();
//	let nowHour = twoDigit(nowTime.getHours());
//	let nowMin = twoDigit(nowTime.getMinutes());
//	let msg = "現在時刻：" + nowHour + ":" + nowMin;
//	document.getElementById("realtime").innerHTML = msg;
//}
//setInterval('showClock()', 1000);
