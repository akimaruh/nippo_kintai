//申請承認後用勤務状況
var statusList = {
	0: "通常出勤",
	1: "休日",
	2: "祝日",
	3: "遅刻",
	4: "有給",
	5: "欠勤",
	6: "早退",
	7: "時間外勤務",
	8: "振替出勤",
	9: "振替休日",
	10: "代替出勤",
	11: "代替休日",

};

function populateStatusDropdown() {
	var dropdowns = document.getElementsByClassName('status-dropdown');
	for (var i = 0; i < dropdowns.length; i++) {
		var dropdown = dropdowns[i];
		var selectedStatus = dropdown.getAttribute('data-status');
		var options = '';

		for (var key in statusList) {
			var selected = (key == selectedStatus) ? ' selected' : '';
			options += "<option value='" + key + "'" + selected + ">" + statusList[key] + "</option>";
		}

		dropdown.innerHTML = options;
	}
}

window.onload = populateStatusDropdown;

// 申請情報確認時に使用
document.addEventListener('DOMContentLoaded', function() {
	// テーブル内のすべてのstatusセルを取得
	var statusCells = document.querySelectorAll('td[id^="status-"]');

	// 各statusセルのテキストを変換
	statusCells.forEach(function(cell) {
		var statusCode = cell.textContent.trim();
		var statusText = statusList[statusCode] || "不明";
		cell.textContent = statusText;
	});
});


////「却下」「承認」ボタンの活性化
//document.addEventListener('DOMContentLoaded', function() {
//	const urlParams = new URLSearchParams(window.location.search);
//	const activateButtons = urlParams.get('activateButtons');
//
//	if (activateButtons === 'true') {
//		document.getElementById('reject-btn').disabled = false;
//		document.getElementById('approve-btn').disabled = false;
//	}
//});


// 入力チェック
document.getElementById('display-form').addEventListener('submit', function(event) {
	let year = document.getElementById('id_year').value;
	let month = document.getElementById('id_month').value;
	let validation = true;

	if (year === "") {
		document.getElementById('yearError').innerHTML = "年を入力してください";
		validation = false;
	} else if (year.length !== 4 || isNaN(year)) {
		document.getElementById('yearError').innerHTML = "yyyyのフォーマットで入力してください";
		validation = false;
	} else {
		document.getElementById('yearError').innerHTML = "";
	}

	if (month === "") {
		document.getElementById('monthError').innerHTML = "月を入力してください";
		validation = false;
	} else if (isNaN(month) || month < 1 || month > 12) {
		document.getElementById('monthError').innerHTML = "MMのフォーマットで入力してください";
		validation = false;
	} else {
		document.getElementById('monthError').innerHTML = "";
	}

	if (!validation) {
		event.preventDefault();
	}
});

// 入力内容をローカルストレージに保存
document.querySelector('#display-form').addEventListener('submit', function(event) {
	var year = document.getElementById('id_year').value;
	var month = document.getElementById('id_month').value;

	// 月を2桁にフォーマット
	if (month.length < 2) {
		month = '0' + month;
	}

	var yearMonth = year + '-' + month;

	var hiddenInput = document.getElementById('yearMonth');
	hiddenInput.value = yearMonth;

	sessionStorage.setItem('year', year);
	sessionStorage.setItem('month', month);
	sessionStorage.setItem('yearMonth', yearMonth);
});

// ページ読み込み時にローカルストレージから値を取得して設定
window.addEventListener('load', function() {
	var storedYear = sessionStorage.getItem('year');
	var storedMonth = sessionStorage.getItem('month');
	var storedYearMonth = localStorage.getItem('yearMonth');


	if (storedYear) {
		document.getElementById('id_year').value = storedYear;
	}
	if (storedMonth) {
		// 月を2桁にフォーマットする
		if (storedMonth.length < 2) {
			storedMonth = '0' + storedMonth;
		}
		document.getElementById('id_month').value = storedMonth;
	}
	if (storedYearMonth) {
		var hiddenInput = document.getElementById('yearMonth');
		if (hiddenInput) {
			hiddenInput.value = storedYearMonth;
		}
	}
});

// 戻るボタン
function goBack() {
	window.history.back();
}

//↓元々書いてあったやつ
//document.addEventListener('DOMContentLoaded', function () {
//    const calendarBody = document.getElementById('calendar-body');
//
//
//    let currentMonth = new Date().getMonth();
//    let currentYear = new Date().getFullYear();
//
//    function generateCalendar(month, year) {
//        calendarBody.innerHTML = ''; // カレンダーをリセット
//        let daysInMonth = new Date(2024, 7 + 1, 0).getDate();
//
//        // カレンダーの行を生成
//        for (let date = 1; date <= daysInMonth; date++) {
//            let row = document.createElement('tr');
//
//            // 日付セル
//            let dateCell = document.createElement('td');
//            dateCell.appendChild(document.createTextNode(`${year}-${month + 1}-${date}`));
//            row.appendChild(dateCell);
//
//            // 曜日セル
//            let dayOfWeek = new Date(year, month, date).getDay();
//            let dayOfWeekCell = document.createElement('td');
//            dayOfWeekCell.appendChild(document.createTextNode(['日', '月', '火', '水', '木', '金', '土'][dayOfWeek]));
//            row.appendChild(dayOfWeekCell);
//
//            // 勤務状況セル
//            let workStatusCell = document.createElement('td');
//            let workStatusInput = document.createElement('input');
//            workStatusInput.type = 'text';
//            workStatusCell.appendChild(workStatusInput);
//            row.appendChild(workStatusCell);
//
//            // 出勤時間セル
//            let startTimeCell = document.createElement('td');
//            let startTimeInput = document.createElement('input');
//            startTimeInput.type = 'time';
//            startTimeCell.appendChild(startTimeInput);
//            row.appendChild(startTimeCell);
//
//            // 退勤時間セル
//            let endTimeCell = document.createElement('td');
//            let endTimeInput = document.createElement('input');
//            endTimeInput.type = 'time';
//            endTimeCell.appendChild(endTimeInput);
//            row.appendChild(endTimeCell);
//
//            // 備考セル
//            let noteCell = document.createElement('td');
//            let noteInput = document.createElement('input');
//            noteInput.type = 'text';
//            noteCell.appendChild(noteInput);
//            row.appendChild(noteCell);
//
//            calendarBody.appendChild(row);
//        }
//    }
//
//    prevMonthButton.addEventListener('click', function () {
//        if (currentMonth === 0) {
//            currentMonth = 11;
//            currentYear--;
//        } else {
//            currentMonth--;
//        }
//        generateCalendar(currentMonth, currentYear);
//    });
//
//    nextMonthButton.addEventListener('click', function () {
//        if (currentMonth === 11) {
//            currentMonth = 0;
//            currentYear++;
//        } else {
//            currentMonth++;
//        }
//        generateCalendar(currentMonth, currentYear);
//    });
//
//    generateCalendar(currentMonth, currentYear);
//});