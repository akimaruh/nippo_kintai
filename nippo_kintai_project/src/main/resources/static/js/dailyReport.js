// 対象日付の初期値を今日の日付に設定
// 参照：https://qiita.com/acht_web/items/cb5edabb5f835fc0c27f
function setTodayDate() {
	var today = new Date();
	var yyyy = today.getFullYear();
	var mm = ("0" + (today.getMonth() + 1)).slice(-2); // getMonth()は1～12月を0～11で返すので+1している
	var dd = ("0" + today.getDate()).slice(-2); // 頭に0をつけてから下2桁を取得
	document.getElementById("today").value = yyyy + '-' + mm + '-' + dd;
}
window.onload = setTodayDate;

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
	inpId.name = `dailyReportDetail[${rowCount}].id`; // name属性を設定
	tr.appendChild(inpId);
	//userId
	let inpUserId = document.createElement("input");
	inpUserId.type = "hidden";
	inpUserId.name = `dailyReportDetail[${rowCount}].userId`; // name属性を設定
	tr.appendChild(inpUserId);
	//date
	let inpDate = document.createElement("input");
	inpDate.type = "hidden";
	inpDate.name = `dailyReportDetail[${rowCount}].date`; // name属性を設定
	tr.appendChild(inpDate);

	// 1つ目のtd
	let td1 = document.createElement("td");
	let inp1 = document.createElement("input");
	inp1.type = "number";
	inp1.className = "form-control";
	inp1.step = "0.5"
	inp1.name = `dailyReportDetail[${rowCount}].time`; // name属性を設定
	td1.appendChild(inp1);
	tr.appendChild(td1);

	// 2つ目のtd
	let td2 = document.createElement("td");
	let inp2 = document.createElement("input");
	inp2.type = "text";
	inp2.className = "form-control";
	inp2.name = `dailyReportDetail[${rowCount}].content`; // name属性を設定
	td2.appendChild(inp2);
	tr.appendChild(td2);

	tblBody.appendChild(tr);
}

//末尾行削除
function del() {
	let rows = tblBody.getElementsByTagName('tr');
	if (rows.length > 0) {
		tblBody.deleteRow(rows.length - 1);
	}
};

//「提出」ボタン：1行でも作業時間・内容が入力されていれば活性化
document.addEventListener("DOMContentLoaded", function() {
	var submitBtn = document.getElementById("submitBtn");
	var dailyReportTbl = document.getElementById("dailyReportTbl");

	// テーブル内に2列とも埋まっている行があるかチェックし、ボタンの状態を更新する
	function updateSubmitBtn() {
		var rows = dailyReportTbl.querySelectorAll("tbody tr");
		var isAnyRowComplete = false; // 少なくとも1行が全て埋まっているかどうかのフラグ

		for (var i = 0; i < rows.length; i++) {
			var inputs = rows[i].querySelectorAll("input"); // 現在の行に含まれる全てのinputタグ要素を取得
			var isRowComplete = true; // 現在の行の全てが埋まっているかどうかのフラグ

			for (var j = 0; j < inputs.length; j++) {
				if (inputs[j].value === "") { // 空の文字列をチェック
					isRowComplete = false;
					break;
				}
			}

			if (isRowComplete) {
				isAnyRowComplete = true;
				break;
			}
		}

		submitBtn.disabled = !isAnyRowComplete; // 1行でも埋まっていればボタンを活性化
	}

	// input=入力された時(値が更新された時)にupdateSubmitBtnを呼び出す
	dailyReportTbl.addEventListener("input", updateSubmitBtn);
});

