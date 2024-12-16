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
		//各種帳票ボタンの活性非活性制御
		let select = document.querySelector('select[name="userId"]');
		let newevent = new Event('input');
		select.dispatchEvent(newevent);
	}, 250);
});

//ユーザーキーワード検索
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
			//			console.log(data);
			const select = document.querySelector('select[name="userId"]');
			select.innerHTML = ''; // 既存のオプションをクリア

			// 新しいオプションを追加
			for (const key in data) {
				//				console.log(key);
				//console.log(key);
				if (data.hasOwnProperty(key)) {
					const option = document.createElement('option');
					option.value = data[key].id;
					option.textContent = data[key].name + '(' + key + ')';
					select.appendChild(option);

				}
			}
			return;
		})
		.catch(error => {
			console.error('エラー:', error);

		});
}

let select = document.querySelector('select[name="userId"]');
function updateButtonState() {
    const allButtons = document.querySelectorAll('button[form="output-form"]');
    console.dir(select);
    console.dir(select.selectedIndex);
    if (select.selectedIndex == -1) {
        allButtons.forEach(button => {
            button.disabled = true; // ボタンを非活性化
        });
    } else {
        allButtons.forEach(button => {
            button.disabled = false; // ボタンを活性化
        });
    }
}

["change", "DOMContentLoaded"].forEach((eventType) => {
    window.addEventListener(eventType, updateButtonState);
});

//ユーザー名欄入力orロード時各種帳票ボタン活性非活性判定
//let select = document.querySelector('select[name="userId"]');
//["input", "DOMContentLoaded"].forEach((eventType) => {
//	select.addEventListener(eventType, () => {
//		//		let select = document.querySelector('select[name="userId"]');
//		const allButtons = document.querySelectorAll('button[form="output-form"]');
//		//		console.log('ボタン活性化',select.children.length);
//		console.dir(select);
//		console.dir(select.selectedIndex);
//		if (select.selectedIndex == -1) {
//			allButtons.forEach(button => {
//				button.disabled = true; // ボタンを活性化
//
//			})
//		} else {
//			allButtons.forEach(button => {
//				button.disabled = false; // ボタンを非活性化
//			})
//		}
//	})
//});

//いずれかの帳票ボタン押下後、該当の帳票がない場合社員番号をキーワードに入れて再度ユーザー名を選択
document.addEventListener('DOMContentLoaded', function() {
	let keyword = document.getElementById('user-search').value;
	if (keyword != '') {
		userSearch();
		document.getElementById('user-search').value = '';

	}
});


//対象年月変更時、各帳票ボタンの活性化判定
const targetYearMonth = document.getElementById('targetYearMonth');
const allButtons = document.querySelectorAll('button[form="output-form"]');
targetYearMonth.addEventListener('change', () => {
	let yearMonthValue = targetYearMonth.value;
	//	let selectValue = select.value;
	//	console.log(isEmpty(select));
	//	console.dir(select);
	if (select.children.length > 0 && yearMonthValue != '') {
		allButtons.forEach(button => {
			button.disabled = false; // ボタンを活性化
		})
	} else {
		allButtons.forEach(button => {
			button.disabled = true; // ボタンを非活性化
		})
	}

})