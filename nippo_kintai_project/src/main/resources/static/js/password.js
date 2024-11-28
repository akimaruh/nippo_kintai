/**
 * パスワード強度チェック
 */
document.getElementById('newPassword').addEventListener('input', function(event) {
	const newPassword = event.target.value;
	const strengthBar = document.getElementById('passwordStrength');
	const strengthMessage = document.getElementById('strengthMessage');

	// パスワードが入力されていないとき
	if (newPassword.length === 0) {
		strengthBar.style.width = "0%";
		strengthBar.setAttribute("aria-valuenow", 0);
		strengthBar.className = ""; // クラスをリセット
		strengthMessage.textContent = ""; // 初期メッセージ
		return;
	}

	// zxcvbnでパスワードを解析
	const result = zxcvbn(newPassword);

	// スコアに基づいて強度を設定(0~4)
	const strengthLevels = [
		{ percent: 0, color: "bg-danger", message: "パスワードを入力してください" },
		{ percent: 25, color: "bg-danger", message: "とても弱いです（英字や数字を組み合わせましょう）" },
		{ percent: 50, color: "bg-warning", message: "弱いです（記号や大文字を追加すると良いでしょう）" },
		{ percent: 75, color: "bg-info", message: "普通ですが、もう少し強化できます（長さを増やしましょう）" },
		{ percent: 100, color: "bg-success", message: "良いパスワードです！" },
	];

	const strength = strengthLevels[result.score];

	// バーを更新
	strengthBar.style.width = strength.percent + "%";
	strengthBar.setAttribute("aria-valuenow", strength.percent);
	strengthBar.className = "progress-bar"; // クラスをリセット
	strengthBar.classList.add(strength.color);

	// メッセージを更新
	strengthMessage.textContent = strength.message;
});


/**
 * 入力チェック系
 */
// ↓完全じゃないので要修正
// 新しいパスワードと確認用パスワードのリアルタイムチェック
//document.getElementById('confirmPassword').addEventListener('input', function() {
//	const newPassword = document.getElementById('newPassword').value;
//	const confirmPassword = document.getElementById('confirmPassword').value;
//	const confirmPasswordError = document.getElementById('confirmPasswordError');
//
//	if (newPassword === confirmPassword) {
//		confirmPasswordError.textContent = ""; // エラーメッセージを消す
//	} else {
//		confirmPasswordError.textContent = "パスワードが一致しません"; // エラー表示
//	}
//});

// 現在のパスワードと新しいパスワードが一致していないかチェック
//document.getElementById('newPassword').addEventListener('input', function() {
//	const currentPassword = document.getElementById('currentPassword').value;
//	const newPassword = document.getElementById('newPassword').value;
//	const currentPasswordError = document.getElementById('currentPasswordError');
//	
//	if (currentPassword === "" && newPassword === "") {
//		currentPasswordError.textContent = "";
//		return;
//	}
//
//	if (currentPassword === newPassword) {
//		currentPasswordError.textContent = "現在のパスワードと同じパスワードは使用できません";
//	} else {
//		currentPasswordError.textContent = "";
//	}
//});