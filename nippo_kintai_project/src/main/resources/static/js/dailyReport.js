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
const inputDate = document.getElementById("today");

inputDate.addEventListener('change', (event) => {
	// 今日の日付を取得（"yyyy-mm-dd"形式）
	var today = new Date().toLocaleDateString("ja-JP", {
		year: "numeric", month: "2-digit",
		day: "2-digit"
	}).split('/').join('-');

	// 日付のバリデーション
	let validation = true;
	
	if (inputDate.value > today) {
		document.getElementById('dateError').innerHTML = "今日以前の日報を選んでください";
		validation = false;

		// 入力行を削除
		del();
	} else {
		document.getElementById('dateError').innerHTML = ""; // エラーメッセージをクリア
	}

	if (!validation) {
		event.preventDefault();
	} else {
		// フォームを送信
		document.getElementById("dailyReport").submit();
	}
});
//入力行を削除・追加ボタンを非表示にする関数
function del() {
	const tblBody = document.getElementById("dailyReport-tbody"); // テーブルのtbodyのIDを指定してください
	let rows = tblBody.getElementsByTagName('tr');
	let addButton = document.getElementById("add-button");

	// 全ての行を削除
	while (rows.length > 0) {
		tblBody.deleteRow(0);
	}
	// addButtonを非表示にする
	addButton.style.display = "none";
}
//日付を別のinputタブに反映
document.getElementById('dailyReport-form').addEventListener('submit', function() {
	const date = document.getElementById('today').value;
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
	const date = document.getElementById('today').value;
	document.getElementById('hidden-date').value = date;
	return true;
});

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

	// 1つ目のtd
	let td1 = document.createElement("td");
	let inp1 = document.createElement("input");
	inp1.type = "number";
	inp1.className = "form-control";
	inp1.min = "0"
	inp1.name = `dailyReportFormDetailList[${rowCount}].time`; // name属性を設定
	td1.appendChild(inp1);
	tr.appendChild(td1);

	// 2つ目のtd
	let td2 = document.createElement("td");
	let inp2 = document.createElement("input");
	inp2.type = "text";
	inp2.className = "form-control";
	inp2.name = `dailyReportFormDetailList[${rowCount}].content`; // name属性を設定
	td2.appendChild(inp2);
	tr.appendChild(td2);

	tblBody.appendChild(tr);
	rowCount++;
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

//「提出」ボタン：活性化/非活性
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
			var isAnyRowPartiallyFilled = false; // いずれかの行が列の1つだけが埋まっているかどうかのフラグ

			if (statusName === '承認済') {
				submitBtn.disabled = true;

				// 行の入力フィールドを全て非活性にする
				for (var i = 0; i < rows.length; i++) {
					var inputs = rows[i].querySelectorAll("input");
					for (var j = 0; j < inputs.length; j++) {
						inputs[j].disabled = true;
					}
				}
				return;
			}

			// '承認済' でない場合、行の入力フィールドを活性にする
			for (var i = 0; i < rows.length; i++) {
				var inputs = rows[i].querySelectorAll("input");
				for (var j = 0; j < inputs.length; j++) {
					inputs[j].disabled = false;
				}
			}

			// 行の入力フィールドをチェック
			for (var i = 0; i < rows.length; i++) {
				var inputs = rows[i].querySelectorAll("input"); // 現在の行のすべての入力フィールドを取得
				var nonEmptyInputsCount = 0; // 現在の行で空でない入力フィールドのカウント

				for (var j = 0; j < inputs.length; j++) {
					if (inputs[j].value !== "") { // 入力フィールドが空でないかチェック
						nonEmptyInputsCount++;
					}
				}

				// 現在の行にデータがあれば、isAllRowsEmptyをfalseに設定
				if (nonEmptyInputsCount > 0) {
					isAllRowsEmpty = false;
				}

				// 現在の行の入力フォームのうち、1列だけ埋まっている場合のフラグ設定
				if (nonEmptyInputsCount === 1) {
					isAnyRowPartiallyFilled = true;
				}
			}

			// shouldDisableButtonは非活性にするべきか、するための条件を満たしているかみたいな意味
			var shouldDisableButton = isAllRowsEmpty !== isAnyRowPartiallyFilled;
			submitBtn.disabled = shouldDisableButton;
		}

		// input=入力された時(値が更新された時)にupdateSubmitBtnを呼び出す
		dailyReportTbl.addEventListener("input", updateSubmitBtn);

		// ページロード時にボタンの状態を更新する
		updateSubmitBtn();
	}
});