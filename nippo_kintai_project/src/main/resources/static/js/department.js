/**
 * ボタンの活性非活性
 */
//登録済みの部署名入力欄
const inputRegistedDepartment = document.getElementById('input-registed-department');
//新部署名入力欄
const inputNewDepartment = document.getElementById('input-new-department');
//削除済部署名入力欄
const inputDeletedDepartment = document.getElementById('deleted-department');

//登録ボタン
const registButton = document.getElementById('regist-btn');
//変更ボタン
const modifyButton = document.getElementById('modify-btn');
//削除ボタン
const deleteButton = document.getElementById('delete-btn');
//有効化ボタン
const activateButton = document.getElementById('activation-btn');


// ボタンの状態を更新する関数
function updateButtonState() {

	// 各入力欄の値をトリム（前後のスペースを削除）したものを使う
	const newDepartmentValue = inputNewDepartment.value.trim();
	const registedDepartmentValue = inputRegistedDepartment.value.trim();
	const deletedDepartmentValue = inputDeletedDepartment.value.trim();

	// 新部署名が入力されていて、登録済み部署名が空である場合に登録ボタンを活性化
	if (newDepartmentValue !== '' && registedDepartmentValue === '') {
		registButton.disabled = false;
	} else {
		registButton.disabled = true;
	}
	//新部署名も登録済み部署名も入力されている場合に変更ボタンを活性化
	// 両方の入力がある場合にのみチェック
	if (newDepartmentValue !== '' && registedDepartmentValue !== '') {
		// 新部署名と登録済み部署名が異なる場合にボタンを活性化
		if (inputRegistedDepartment.value != inputNewDepartment.value) {
			modifyButton.disabled = false;
		} else {
			modifyButton.disabled = true;
		}
	} else {
		// どちらかが空の場合はボタンを非活性化
		modifyButton.disabled = true;
	}
	//新部署名が空で登録済み部署名が入力されている場合に削除ボタンを活性化
	if (newDepartmentValue === '' && registedDepartmentValue !== '') {
		deleteButton.disabled = false;
	} else {
		deleteButton.disabled = true;
	}
	//削除済み部署名が入力されている場合に有効化ボタンを活性化
	if (deletedDepartmentValue !== '') {
		activateButton.disabled = false;
	} else {
		activateButton.disabled = true;
	}

}
//入力チェックエラー時にボタン非活性を強制する関数
function forceDesabled() {
	registButton.disabled = true;
	modifyButton.disabled = true;
	activateButton.disabled = true;
}

// 新部署名、登録済み部署名の両方の入力欄にイベントリスナーを追加
inputNewDepartment.addEventListener('input', updateButtonState);
inputRegistedDepartment.addEventListener('input', updateButtonState);
inputDeletedDepartment.addEventListener('input', updateButtonState);


// ページ読み込み時に初期状態をチェック
document.addEventListener('DOMContentLoaded', updateButtonState);

/**
 * 確認ダイアログ
 */
//削除確認
function delcheck() {
	let check = window.confirm(inputRegistedDepartment.value + 'を登録から削除します。本当によろしいですか？');
	if (check) {
		console.log('削除するって');
		return true;
	} else {
		console.log('キャンセルするって');
		return false;
	}
}

function activecheck() {
	let check = window.confirm(inputDeletedDepartment.value + 'を有効化します。よろしいですか？');
	if (check) {
		console.log('削除するって');
		return true;
	} else {
		console.log('キャンセルするって');
		return false;
	}
}
/**
 * 入力チェック
 */
function validation() {

	const MAX_DEPARTMENT_LENGTH = 10;
	const OVER_DEPARTMENT_LENGTH = "100文字以内で入力して下さい"
	const EMPTIY = "";

	if (inputNewDepartment.value.length > MAX_DEPARTMENT_LENGTH) {
		document.getElementById('newNameError').innerHTML = OVER_DEPARTMENT_LENGTH;
		forceDesabled();

	} else {
		document.getElementById('newNameError').innerHTML = EMPTIY;
	}
	//同じ部署名は登録できません。

}

inputNewDepartment.addEventListener('input', validation);
inputRegistedDepartment.addEventListener('input', validation);

/**
 * 入力後次の操作欄に光るアニメーション
 */
const registeredDepartment = document.getElementById('input-registed-department');
const newDepartmentInput = document.getElementById('input-new-department');
//const modifyButton = document.getElementById('modify-btn');
//const registButton = document.getElementById('regist-btn');

// 部署名入力欄にイベントリスナーを追加
registeredDepartment.addEventListener('change', function() {
	if (registeredDepartment.value !== "") {
		// 登録済みの部署名が選択された場合、新部署名欄を光らせる
		newDepartmentInput.classList.add('glowing-input');
	} else {
		// 未選択の場合、新部署名欄の光るアニメーションを削除
		newDepartmentInput.classList.remove('glowing-input');
	}
	checkButtons();
});

newDepartmentInput.addEventListener('input', function() {
	checkButtons();
});

// ボタンの状態を確認して、アニメーションを追加
function checkButtons() {
	const isNewDepartmentEntered = newDepartmentInput.value.trim() !== "";

	if (registeredDepartment.value !== "" && isNewDepartmentEntered) {
		// 登録済み部署名が選択され、新部署名が入力された場合、変更ボタンを光らせる
		modifyButton.disabled = false;
		newDepartmentInput.classList.remove('glowing-input');
		modifyButton.classList.add('glowing-button');
	} else {
		modifyButton.disabled = true;
		modifyButton.classList.remove('glowing-button');
	}

	if (registeredDepartment.value === "" && isNewDepartmentEntered) {
		// 登録済み部署名が未選択で、新部署名が入力された場合、登録ボタンを光らせる
		registButton.disabled = false;
		registButton.classList.add('glowing-button');
	} else {
		registButton.disabled = true;
		registButton.classList.remove('glowing-button');
	}
}




