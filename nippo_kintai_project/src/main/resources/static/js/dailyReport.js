// 対象日付の初期値を今日の日付に設定
//function setTodayDate() {
//	var dateInput = document.getElementById("today");
//
//	// 日付がまだ設定されていない場合にのみ今日の日付を設定する
//	if (!dateInput.value) {
//		var today = new Date();
//		var yyyy = today.getFullYear();
//		var mm = ("0" + (today.getMonth() + 1)).slice(-2); // 月を0埋めして2桁に
//		var dd = ("0" + today.getDate()).slice(-2); // 日を0埋めして2桁に
//		dateInput.value = yyyy + '-' + mm + '-' + dd;
//	}
//}
//window.onload = setTodayDate;

//カレンダー選択後日報表示反映
const inputDate = document.getElementById("date");
inputDate.addEventListener('change', (event) => {

	// 今日の日付を取得（"yyyy-mm-dd"形式）
	var today = new Date().toLocaleDateString("ja-JP", {
		year: "numeric", month: "2-digit",
		day: "2-digit"
	}).split('/').join('-');
	// 日付のバリデーション
	const inputDateValue = new Date(inputDate.value);
	const todayDate = new Date(today);
	// 日付のバリデーション
	let validation = true;

	if (inputDateValue > todayDate || inputDateValue == '') {
		document.getElementById('dateError').innerHTML = "今日以前の日報を選んでください";
		validation = false;
		// 入力行を削除・追加ボタンを非表示
		del();
	} else {
		document.getElementById('dateError').innerHTML = ""; // エラーメッセージをクリア
	}

	if (validation) {
		// フォームを送信
		document.getElementById("dailyReport").submit();

	} else {
		event.preventDefault();

	}
});
//入力行を削除・追加ボタンを非表示にする関数
function del() {
	const tblBody = document.getElementById("dailyReport-tbody"); // テーブルのtbodyのIDを指定してください
	let rows = tblBody.getElementsByTagName('tr');
	let addButton = document.getElementById("addBtn");
	let submitButton = document.getElementById("submitBtn");

	// 全ての行を削除
	while (rows.length > 0) {
		tblBody.deleteRow(0);
	}
	// addButtonを非表示にする
	addButton.style.display = "none";
	submitButton.style.display = "none";
}
//日付を別のinputタブに反映
let dailyReportForm = document.getElementById('dailyReport-form');
if (dailyReportForm != null) {
	dailyReportForm.addEventListener('submit', function() {
		const date = document.getElementById('date').value;
		// 同じクラス名を持つすべての要素を取得
		const hiddenDateElements = document.getElementsByClassName('hidden-date');

		// すべての要素に対して値を設定
		for (let i = 0; i < hiddenDateElements.length; i++) {
			hiddenDateElements[i].value = date;
		}
		return true;
	});


//日付を別のinputタブに反映
document.getElementById('dailyReport-form').addEventListener('submit', function() {
	const date = document.getElementById('date').value;
	document.getElementById('hidden-date').value = date;
	return true;
})
};

// テーブルの行追加・削除
// 参照：https://qiita.com/forever---searcher/items/7901217dc811d72687f8
const tblBody = document.getElementById("dailyReportTbl").getElementsByTagName('tbody')[0];
let rowCount = tblBody.getElementsByTagName('tr').length; // 既存の行数をカウント

//行追加
function add() {
	let tr = document.createElement("tr");
	//id
	let inpId = document.createElement("input");
	inpId.type = "hidden";
	inpId.name = `dailyReportFormDetailList[${rowCount}].id`; // name属性を設定
	tr.appendChild(inpId);
	//userId
	let inpUserId = document.createElement("input");
	inpUserId.type = "hidden";
	inpUserId.name = `dailyReportFormDetailList[${rowCount}].userId`; // name属性を設定
	tr.appendChild(inpUserId);
	//date
	let inpDate = document.createElement("input");
	inpDate.type = "hidden";
	inpDate.name = `dailyReportFormDetailList[${rowCount}].date`; // name属性を設定
	inpDate.value = inputDate.innerHTML;
	tr.appendChild(inpDate);

	//	作業の列
	let td1 = document.createElement("td");
	let selct1 = document.createElement("select");

	// workMapからオプションを生成して追加
	selct1.innerHTML = createSelectOptions(workArray);
	selct1.className = "form-select";
	selct1.name = `dailyReportFormDetailList[${rowCount}].workId`; // name属性を設定
	td1.appendChild(selct1);
	//	selct1.appendChild(op1);
	tr.appendChild(td1);
	// 作業時間の列
	let td2 = document.createElement("td");
	let inp2 = document.createElement("input");
	inp2.type = "number";
	inp2.className = "form-control";
	inp2.min = "0"
	inp2.name = `dailyReportFormDetailList[${rowCount}].time`; // name属性を設定
	td2.appendChild(inp2);
	tr.appendChild(td2);

	// 作業内容の列
	let td3 = document.createElement("td");
	let inp3 = document.createElement("input");
	inp3.type = "text";
	inp3.className = "form-control";
	inp3.name = `dailyReportFormDetailList[${rowCount}].content`; // name属性を設定
	td3.appendChild(inp3);
	tr.appendChild(td3);

	tblBody.appendChild(tr);
	rowCount++;
}

//プルダウンリストの作成
function createSelectOptions(workArray) {
	let options = '<option value=""></option>'; // 空のオプションを追加
	workArray.forEach(({ key, value }) => {
		options += `<option value="${value}">${key}</option>`;
	});
	return options; // 生成したオプションのHTMLを返す
}
//ダメだったらこっち試す
//	function add() {
//    const table = document.getElementById('reportTable');
//    const newRow = table.insertRow();
//    const index = table.rows.length - 1; // 新しいインデックスを取得
//
//    newRow.innerHTML = `
//        <td><input type="number" step="0.5" name="DailyReportFormDetailList[${index}].time" /></td>
//        <td><input type="text" name="DailyReportFormDetailList[${index}].content" /></td>
//    `;
//}

//末尾行削除
//function del() {
//	let rows = tblBody.getElementsByTagName('tr');
//	if (rows.length > 0) {
//		tblBody.deleteRow(rows.length - 1);
//	}
//};

////「提出」ボタン：活性化/非活性
document.addEventListener("DOMContentLoaded", function() {
	var submitBtn = document.getElementById("submitBtn");
	var dailyReportTbl = document.getElementById("dailyReportTbl");
	var statusNameElement = document.getElementById("statusName");

	// statusNameElement が nullでない場合のみ処理を実行する
	if (statusNameElement) {
		var statusName = statusNameElement.textContent.trim(); // statusNameの取得

		function updateSubmitBtn() {
			var rows = dailyReportTbl.querySelectorAll("tbody tr");
			var isAllRowsEmpty = true; // 全ての行が空であるかどうかのフラグ
			var isAnyRowPartiallyFilled = false; // いずれかの行のいずれかの列が埋まっているかどうかのフラグ

			if (statusName === '承認済') {
				submitBtn.disabled = true;

				// 行の入力フィールドを全て非活性にする
				for (var i = 0; i < rows.length; i++) {
					var inputs = rows[i].querySelectorAll("input,select");
					for (var j = 0; j < inputs.length; j++) {
						inputs[j].disabled = true;
					}
				}
				return;
			}

			// '承認済' でない場合、行の入力フィールドを活性にする
			for (var i = 0; i < rows.length; i++) {
				var inputs = rows[i].querySelectorAll("input,select");
				for (var j = 0; j < inputs.length; j++) {
					inputs[j].disabled = false;
				}
			}
			// 行の入力フィールドをチェック
			for (var i = 0; i < rows.length; i++) {
				var nonEmptyInputsCount = 0; // 現在の行で空でない入力フィールドのカウント
				var columns = rows[i].querySelectorAll("td"); // 現在の行のすべての <td> 要素を取得
				console.log("columns" + columns.length);
				for (var j = 0; j < columns.length; j++) {

					var inputs = columns[j].querySelectorAll("input, select"); // 現在の列のすべての入力フィールドを取得
					for (var k = 0; k < inputs.length; k++) {
						if (inputs[k].value !== "") { // 入力フィールドが空でないかチェック
							console.log("inputs[k].value" + inputs[k].value);
							nonEmptyInputsCount++;
						}
					}
				}
				console.log("rows[" + i + "]入力フィールド何か入ってたら＋１する。nonEmptyInputsCount" + nonEmptyInputsCount);
				console.log("nonEmptyInputsCount < inputs.lengthの真偽");
				console.log(nonEmptyInputsCount < columns.length);

				// 現在の行にデータがあれば、isAllRowsEmptyをfalseに設定
				if (nonEmptyInputsCount > 0) {
					isAllRowsEmpty = false;
					// 現在の行の入力フォームのうち、埋まっている列が１以上列数未満の場合フラグ設定
					if (nonEmptyInputsCount < columns.length) {
						console.log("isAnyRowPartiallyFilled = true 通過します。");
						isAnyRowPartiallyFilled = true;
					}
				}
			}
			//入力フィールドのデータが入っている行において、一つでも空欄があったら提出ボタンは非活性とする。
			var shouldDisableButton = isAllRowsEmpty !== isAnyRowPartiallyFilled;
			submitBtn.disabled = shouldDisableButton;
			console.log("提出ボタン活性非活性確認メソッド発火");
			console.log("isAllRowsEmpty" + isAllRowsEmpty);
			console.log("isAnyRowPartiallyFilled" + isAnyRowPartiallyFilled);
		}

		// input=入力された時(値が更新された時)にupdateSubmitBtnを呼び出す
		dailyReportTbl.addEventListener("input", updateSubmitBtn);
		// ページロード時にボタンの状態を更新する
		updateSubmitBtn();
	}
});