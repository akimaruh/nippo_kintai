
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
