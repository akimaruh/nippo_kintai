/**
 * ページ内リンク
 */
// 「ユーザー」アイコン押下
//const usersForms = document.querySelectorAll('.users-form');
//usersForms.forEach((usersForm) => {
//    usersForm.addEventListener('submit', function(event) {
//        usersForm.action = usersForm.action + '#所属ユーザー';
//    });
//});
//
//// 「復元」ボタン押下
//const restoreForm = document.getElementById('restore-form');
//if (restoreForm) {
//	restoreForm.addEventListener('submit', function(event) {
//		restoreForm.action = restoreForm.action + '#部署の復元';
//	});
//};
	

/**
 * 変更モーダル
 */
document.addEventListener('DOMContentLoaded', function() {
	const modalSetting = document.getElementById('modalSetting');
	const openModal = modalSetting.dataset.openModal;
	const errorMessage = modalSetting.dataset.errorMessage;
	
	// モーダル再表示（エラーメッセージ表示）
	if (openModal === 'true') {
		const modifyModal = new bootstrap.Modal(document.getElementById('modifyModal'));
		modifyModal.show();
		// エラーメッセージをモーダルに表示
		document.getElementById("modalErrorMessage").textContent = errorMessage; // alert alert-dangerのところ
		document.getElementById('newNameError').textContent = newNameErrorMessage ?? ''; // spanタグのところ
		document.getElementById('exsistsName').value = inputNewName; // 入力したnewNameをテキストエリアに表示
	}
	
	// 変更ボタン押下（モーダル開くボタン）
	const modifyBtns = document.querySelectorAll('.modify-btn');
	modifyBtns.forEach((modifyBtn) => {
		modifyBtn.addEventListener('click', function() {
			// ボタンのdata属性から情報を取得
			const departmentName = modifyBtn.getAttribute('data-department-name');
			// モーダルのフォームにデータをセット
			document.getElementById('exsistsName').value = departmentName;
			document.getElementById('hiddenExsistsName').value = departmentName;
		});
	});
});

/**
 * 確認ダイアログ
 */
//削除確認
const deleteBtns = document.querySelectorAll('.delete-btn');
deleteBtns.forEach((deleteBtn) => {
	deleteBtn.addEventListener('click', function(event) {
		// data-*属性から値を取得
		const departmentName = this.dataset.departmentName;
		// 確認ダイアログを表示
		const confirmation = window.confirm(`部署 [${departmentName}] を本当に廃止しますか？`);
		if (!confirmation) {
			event.preventDefault();
		}
	});
});

//復元確認
const restoreBtns = document.querySelectorAll('.restore-btn');
restoreBtns.forEach((restoreBtn) => {
	restoreBtn.addEventListener('click', function(event) {
		const departmentName = this.dataset.departmentName;
		const confirmation = window.confirm(`部署 [${departmentName}] を復元しますか？`);
		if (!confirmation) {
			event.preventDefault();
		}
	})
})

/**
 * その他
 */
// ボタン押下で画面上部に遷移
const pageTopBtn = document.getElementById('pageTopBtn');
pageTopBtn.addEventListener('click', () => {
	window.scroll({
		top: 0,
		behavaior: "smoth",
	});
});


const sortElement = document.getElementById('sort-table');
Sortable.create(sortElement, {
	handle: '.handle', // ドラッグのトリガーをセレクタで指定します
	chosenClass: 'chosen', // ドラッグで選択中の要素に付与するクラス名
	animation: 200, // ドラッグして並び替える時のアニメーションの速さを指定します
	onEnd: () => {
		// 並び替え後の順序を取得
		const orderData = Array.from(sortElement.children).map((row, index) => {
			return {
				departmentId: row.getAttribute('data-department-id'), // 部署ID
				sortOrder: index + 1, // 並び順（1から始める）
			};
		});

		// ユーザーID取得
		//			const userId = /*[[${session.loginUser.id}]]*/ 0;
		console.log(userId);

		// ユーザーIDをorderDataに追加
		orderData.forEach(item => item.userId = userId);

		// AJAXでサーバーに送信
		fetch('/department/saveOrder', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(orderData),
		}).then(response => {
			if (response.ok) {
				console.log('順序保存した');
			} else {
				console.error('順序の保存に失敗しました');
			}
		});

	},
});

//function activecheck() {
//	let check = window.confirm(inputDeletedDepartment.value + 'を復元します。よろしいですか？');
//	if (check) {
//		console.log('削除するって');
//		return true;
//	} else {
//		console.log('キャンセルするって');
//		return false;
//	}
//}

//document.addEventListener('DOMContentLoaded', () => {
//	let targetForm = null; // 削除対象のフォーム
//	
//	// 削除ボタンをクリックしたときの処理
//	const deleteButtons = document.querySelectorAll('.delete-button');
//	deleteButtons.forEach(button => {
//		button.addEventListener('click', event => {
//			// フォームを保持
//			targetForm = button.closest('.delete-form');
//			// モーダルのメッセージを更新
//			const deleteDepartmentName = targetForm.querySelector('.delete-department-name').value;
//			document.getElementById('modalMessage').textContent = `${deleteDepartmentName} を廃止します。本当によろしいですか？`;
//		});
//	});
//	
//	// モーダル内の「削除」ボタンをクリックしたとき
//	document.getElementById('confirmDeleteButton').addEventListener('click', () => {
//		if (targetForm) {
//			targetForm.submit(); // フォームを送信
//		}
//	});
//});


//
///**
// * ボタンの活性非活性
// */
////登録済みの部署名入力欄
//const inputRegistedDepartment = document.getElementById('input-registed-department');
////新部署名入力欄
//const inputNewDepartment = document.getElementById('input-new-department');
////削除済部署名入力欄
//const inputDeletedDepartment = document.getElementById('deleted-department');
//
////登録ボタン
//const registButton = document.getElementById('regist-btn');
////変更ボタン
//const modifyButton = document.getElementById('modify-btn');
////削除ボタン
//const deleteButton = document.getElementById('delete-btn');
////有効化ボタン
//const activateButton = document.getElementById('activation-btn');
////入力欄が空でなければ削除ボタンを活性化して新部署名欄にアニメーションをつける
////入力欄が空なら削除ボタンは非活性化、アニメーションなし
////新部署欄入力後
//document.getElementById('dropdownList').addEventListener('click', function() {
//	const inputRegistedDepartment = document.getElementById('input-registed-department');
//	deleteButton;
//	const inputNewDepartment = document.getElementById('input-new-department');
//	var newDepartment = inputNewDepartment.value;
//	var registeredDepartment = inputRegistedDepartment.value;
//
//	// 入力欄が空でなければ削除ボタンを活性化
//	if (registeredDepartment != "" && newDepartment == "" && registeredDepartment != newDepartment) {
//		deleteButton.disabled = false;
//		inputNewDepartment.classList.add('glowing-input');  // 光るアニメーションを追加
//	} else {
//		deleteButton.disabled = true;
//		inputNewDepartment.classList.remove('glowing-input');  // 光るアニメーションを削除
//	}
//	if (newDepartment != "" && registeredDepartment.trim() != "") {
//		// 登録済み部署名が選択され、新部署名が入力された場合、変更ボタンを光らせる
//		//		modifyButton.disabled = false;
//		//		modifyButton.classList.add('glowing-input');
//	} else {
//		modifyButton.disabled = true;
//		modifyButton.classList.remove('glowing-button');
//	}
//	if (newDepartment != "" && registeredDepartment == "") {
//		// 登録済み部署名が未選択で、新部署名が入力された場合、登録ボタンを光らせる
//		registButton.disabled = false;
//		registButton.classList.add('glowing-button');
//	} else {
//		registButton.disabled = true;
//		registButton.classList.remove('glowing-button');
//	}
//});
//
//document.getElementById('input-new-department').addEventListener('input', function() {
//	console.log('新部署名入力感知');
//	checkButtons();
//});
//
//// ボタンの状態を確認して、アニメーションを追加
//function checkButtons() {
//	//登録済みの部署名入力欄
//	const inputRegistedDepartment = document.getElementById('input-registed-department');
//	//新部署名入力欄
//	const inputNewDepartment = document.getElementById('input-new-department');
//	console.log('checkButtons関数開始');
//
//	var newDepartment = inputNewDepartment.value;
//	var registeredDepartment = inputRegistedDepartment.value;
//
//	if (registeredDepartment != "" && newDepartment.trim() != "") {
//		// 登録済み部署名が選択され、新部署名が入力された場合、変更ボタンを光らせる
//		modifyButton.disabled = false;
//		inputNewDepartment.classList.remove('glowing-input');
//		modifyButton.classList.add('glowing-button');
//	} else {
//		modifyButton.disabled = true;
//		modifyButton.classList.remove('glowing-button');
//	}
//
//	if (inputRegistedDepartment.value == "" && newDepartment != "") {
//		// 登録済み部署名が未選択で、新部署名が入力された場合、登録ボタンを光らせる
//		registButton.disabled = false;
//		registButton.classList.add('glowing-button');
//	} else {
//		registButton.disabled = true;
//		registButton.classList.remove('glowing-button');
//	}
//}
//
//// ページ読み込み時に初期状態をチェック
//document.addEventListener('DOMContentLoaded', updateButtonState);
//
//// 新部署名、登録済み部署名の両方の入力欄にイベントリスナーを追加
//document.getElementById('dropdownList').addEventListener('click', updateButtonState);
//inputNewDepartment.addEventListener('input', updateButtonState);
//inputDeletedDepartment.addEventListener('input', updateButtonState);
//
//// ボタンの状態を更新する関数
//function updateButtonState() {
//
//	// 各入力欄の値をトリム（前後のスペースを削除）したものを使う
//	const newDepartmentValue = inputNewDepartment.value.trim();
//	const registedDepartmentValue = inputRegistedDepartment.value.trim();
//	const deletedDepartmentValue = inputDeletedDepartment.value.trim();
//	console.log(registedDepartmentValue);
//	console.log(deletedDepartmentValue);
//	//新部署名が空で登録済み部署名が入力されている場合に削除ボタンを活性化
//	if (registedDepartmentValue !== '' && newDepartmentValue === '') {
//		deleteButton.disabled = false;
//	} else {
//		deleteButton.disabled = true;
//	}
//	// 新部署名が入力されていて、登録済み部署名が空である場合に登録ボタンを活性化
//	if (newDepartmentValue !== '' && registedDepartmentValue === '') {
//		registButton.disabled = false;
//	} else {
//		registButton.disabled = true;
//	}
//	//新部署名も登録済み部署名も入力されている場合に変更ボタンを活性化
//	// 両方の入力がある場合にのみチェック
//	if (newDepartmentValue !== '' && registedDepartmentValue !== '') {
//		// 新部署名と登録済み部署名が異なる場合にボタンを活性化
//		if (inputRegistedDepartment.value != inputNewDepartment.value) {
//			modifyButton.disabled = false;
//		} else {
//			modifyButton.disabled = true;
//		}
//	} else {
//		// どちらかが空の場合はボタンを非活性化
//		modifyButton.disabled = true;
//	}
//
//	//削除済み部署名が入力されている場合に有効化ボタンを活性化
//	if (deletedDepartmentValue !== '') {
//		activateButton.disabled = false;
//	} else {
//		activateButton.disabled = true;
//	}
//
//}
////入力チェックエラー時にボタン活性・アニメーションを強制停止する関数
//function forceDesabled() {
//	registButton.disabled = true;
//	modifyButton.disabled = true;
//	activateButton.disabled = true;
//	inputNewDepartment.classList.remove('glowing-input');
//	modifyButton.classList.remove('glowing-button');
//	registButton.classList.remove('glowing-button');
//
//}
//
//
//
///**
// * 確認ダイアログ
// */
////削除確認
//function delcheck() {
//	let check = window.confirm(inputRegistedDepartment.value + 'を廃止します。本当によろしいですか？');
//	if (check) {
//		console.log('削除するって');
//		return true;
//	} else {
//		console.log('キャンセルするって');
//		return false;
//	}
//}
//
//function activecheck() {
//	let check = window.confirm(inputDeletedDepartment.value + 'を復元します。よろしいですか？');
//	if (check) {
//		console.log('削除するって');
//		return true;
//	} else {
//		console.log('キャンセルするって');
//		return false;
//	}
//}
///**
// * 入力チェック
// */
//
//document.getElementById('input-new-department').addEventListener('input', validation);
//document.getElementById('dropdownList').addEventListener('click', validation);
//
//function validation() {
//	//登録済みの部署名入力欄
//	const inputRegistedDepartment = document.getElementById('input-registed-department');
//	//新部署名入力欄
//	const inputNewDepartment = document.getElementById('input-new-department');
//
//	const MAX_DEPARTMENT_LENGTH = 100;
//	const OVER_DEPARTMENT_LENGTH = "100文字以内で入力して下さい"
//	const CANNOT_SAME_NAME = "同じ部署名は登録できません";
//	const EMPTIY = "";
//
//	if (inputNewDepartment.value.trim().length > MAX_DEPARTMENT_LENGTH) {
//		document.getElementById('newNameError').innerHTML = OVER_DEPARTMENT_LENGTH;
//		forceDesabled();
//	} else if ((inputRegistedDepartment.value != "" && inputNewDepartment.value != "") && inputRegistedDepartment.value == inputNewDepartment.value) {
//		document.getElementById('newNameError').innerHTML = CANNOT_SAME_NAME;
//		forceDesabled();
//	} else {
//		document.getElementById('newNameError').innerHTML = EMPTIY;
//	}
//}
////同じ部署名は登録できません。
//
//
//
//
//
//////ここから更新してる
///**
// * ドロップダウンの表示・非表示の切り替え
// */
//
//inputRegistedDepartment.addEventListener('click', function() {
//	var dropdownContent = document.getElementById('dropdownList');
//	if (dropdownContent.style.display === "block") {
//		dropdownContent.style.display = "none";
//	} else {
//		dropdownContent.style.display = "block";
//	}
//});
//
//// 検索フィルタの適用
//document.querySelector('.dropdown-content input').addEventListener('keyup', function() {
//	var filter = this.value.toUpperCase();
//	var links = document.querySelectorAll('.dropdown-content div');
//	links.forEach(function(link) {
//		if (link.id !== 'emptyOption') {
//			if (link.textContent.toUpperCase().indexOf(filter) > -1) {
//				link.style.display = "";
//			} else {
//				link.style.display = "none";
//			}
//		}
//	});
//});
//
//// 候補を選択した際の処理
//document.querySelectorAll("#dropdownList div").forEach(function(option) {
//	option.addEventListener("click", function() {
//		var input = inputRegistedDepartment;
//		input.value = this.textContent; // 選択した項目を入力欄に反映
//
//		var searchInput = document.querySelector('.dropdown-content input');
//		searchInput.value = ""; // 検索窓の内容をクリア
//
//		// フィルタリングされた選択肢を元に戻す
//		var links = document.querySelectorAll('.dropdown-content div');
//		links.forEach(function(link) {
//			link.style.display = ""; // すべての選択肢を表示
//		});
//
//		document.getElementById("dropdownList").style.display = "none"; // リストを閉じる
//	});
//
//	// ドロップダウン外をクリックした場合に閉じる
//	document.addEventListener("click", function(event) {
//		var dropdown = document.getElementById("dropdownList");
//		var searchInput = inputRegistedDepartment;
//
//		// クリックされた場所が検索窓やドロップダウンリストでない場合、ドロップダウンを閉じる
//		if (!searchInput.contains(event.target) && !dropdown.contains(event.target)) {
//			dropdown.style.display = "none";
//		}
//	});
//});
//
//
