//キーワード入力後あいまい検索
const inputUserKeyword = document.getElementById("user-search");
let typingTimer; // タイピングタイマー

inputUserKeyword.addEventListener('input', () => {
	clearTimeout(typingTimer);

	typingTimer = setTimeout(() => {
		if (inputUserKeyword.value != '') {
			userSearch();
		}
		// フォーム送信後に再度フォーカスを設定
		inputUserKeyword.focus();
		//各種日報集計ボタン活性メソッド
		let allButtons = document.querySelectorAll('button');
		let select = document.getElementsByClassName('select');
		console.log(select);
		if (select.innerHTML != '') {
			console.log("セレクト何か入った");
			console.log(allButtons);
			allButtons.forEach(button => {
				button.disabled = false; // ボタンを活性化

			})
		} else {
			allButtons.forEach(button => {
				button.disabled = true; // ボタンを活性化
			})
		}
	}, 250);
});

function userSearch() {
	const keyword = document.getElementById('user-search').value;
	// Ajaxリクエストを非同期で送信
	fetch('/output/userSearch', {

		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			"userKeyword": keyword,
		}),
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('処理に失敗しました');
			}
			return response.json();

		})
		.then(data => {
			console.log(data);
			const select = document.querySelector('select[name="userId"]');
			select.innerHTML = ''; // 既存のオプションをクリア

			// 新しいオプションを追加
			for (const key in data) {
				console.log(key);
				//console.log(key);
				if (data.hasOwnProperty(key)) {
					const option = document.createElement('option');
					option.value = data[key];
					option.textContent = key;
					select.appendChild(option);
				}
			}
		})
		.catch(error => {
			console.error('エラー:', error);

		});
}