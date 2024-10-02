
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

function markAsReadForModal(event, notificationId, userId,index) {
	event.preventDefault(); // デフォルトのリンク遷移を防止

	// 通知の内容を取得（必要に応じてサーバーから取得する処理に変更）
	
    const message = document.getElementById("notificationsList["+index+"].message").innerHTML;
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
