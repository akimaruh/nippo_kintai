/**
 * ボタンの活性非活性
 */
//登録済みの部署名入力欄
const inputRegistedDepartment = document.getElementById('input-registed-department');
//新部署名入力欄
const inputNewDepartment = document.getElementById('input-new-department');
//登録ボタン
const registButton = document.getElementById('regist-btn');
//変更ボタン
const modifyButton = document.getElementById('modify-btn');
//削除ボタン
const deleteButton = document.getElementById('delete-btn');

// ボタンの状態を更新する関数
function updateButtonState() {

	// 各入力欄の値をトリム（前後のスペースを削除）したものを使う
	const newDepartmentValue = inputNewDepartment.value.trim();
	const registedDepartmentValue = inputRegistedDepartment.value.trim();

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

}
//入力チェックエラー時にボタン非活性を強制する関数
function forceDesabled() {
	registButton.disabled = true;
	modifyButton.disabled = true;
}

// 新部署名、登録済み部署名の両方の入力欄にイベントリスナーを追加
inputNewDepartment.addEventListener('input', updateButtonState);
inputRegistedDepartment.addEventListener('input', updateButtonState);

// ページ読み込み時に初期状態をチェック
document.addEventListener('DOMContentLoaded', updateButtonState);

/**
 * 確認ダイアログ
 */
//削除確認
function delcheck() {
	let check= window.confirm('削除します。よろしいですか？');
	if(check){
		console.log('削除するって');
		return true;
	}else{
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

}

inputNewDepartment.addEventListener('input', validation);
inputRegistedDepartment.addEventListener('input', validation);





