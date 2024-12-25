//申請承認後用勤務状況(マネージャ権限)
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
//勤怠訂正モーダル表示で勤怠状況プルダウンを作成・選択項目反映
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
const form = document.getElementById('display-form');
if (form) {
	form.addEventListener('submit', function(event) {

		//document.getElementById('display-form').addEventListener('submit', function(event) {
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
}

// 【訂正モーダル】モーダルの表示、「申請」ボタン押下でフォーム送信
document.addEventListener('DOMContentLoaded', function() {

	// 成功メッセージの表示処理
	const message = localStorage.getItem('message');
	if (message) {
		const messageContainer = document.getElementById('message-container');
		messageContainer.classList.add('alert', 'alert-info');
		messageContainer.textContent = message;
		messageContainer.style.display = 'block';
		localStorage.removeItem('message');
	}

	// 勤怠表の行を取得
	const rows = document.querySelectorAll('.attendance-row');

	rows.forEach(row => {
		row.addEventListener('click', function() {
			// クリックした行から日付と曜日とステータスを取得
			const date = this.getAttribute('data-date');
			const week = this.getAttribute('data-week');
			const status = this.getAttribute('data-status');

			if (status == '2') { // 承認済みステータス

				// 日付と曜日を組み合わせてモーダルに表示
				const modalData = `${date}(${week})`;
				document.getElementById('modal-data').textContent = modalData;

				// hiddenフィールドに対象日付と今日の日付をセット
				document.getElementById('correction-date').value = date;
				document.getElementById('correction-application-date').value = new Date().toISOString();

				const correctionModal = new bootstrap.Modal(document.getElementById('correctionModal'));
				correctionModal.show();
			}
		});
	});

	// 入力チェック
	document.getElementById('submit-correction').addEventListener('click', function() {
		let startTime = document.getElementById('correctionStartTime').value;
		let endTime = document.getElementById('correctionEndTime').value;
		let remarks = document.getElementById('correctionRemarks').value;
		let correctionReason = document.getElementById('correctionCorrectionReason').value;
		let status = Number(document.getElementById('correctionStatus').value); // 数値に変換

		const MAX_REMARKS_LENGTH = 20;
		const MAX_REASON_LENGTH = 20;

		// エラーメッセージと該当フォームに赤枠つける
		function showError(inputId, errorMessage) {
			document.getElementById(inputId + 'Error').innerHTML = errorMessage;
			document.getElementById(inputId).classList.add("form-control-alt", "is-invalid");
			document.getElementById('correctionAlert').classList.add("alert", "alert-danger");
		}

		// エラーメッセージをクリアし、赤枠を削除
		function clearErrors() {
			const errorFields = ['correctionStartTime', 'correctionEndTime', 'correctionRemarks', 'correctionCorrectionReason', 'correctionStatus'];
			errorFields.forEach(field => {
				document.getElementById(field + 'Error').innerHTML = "";
				document.getElementById(field).classList.remove("form-control-alt", "is-invalid");
			});
		}
		clearErrors();
		let isValid = true;

		// 休日系統と出勤系統
		let holidaySystem = [1, 2, 4, 5, 9, 11];
		let attendanceSystem = [0, 3, 6, 7, 8, 10];

		// 休日系統
		if (holidaySystem.includes(status)) {
			if (startTime || endTime) {
				showError('correctionStartTime', '休日に勤務時間は入力できません。');
				isValid = false;
			}

			// 出勤系統
		} else if (attendanceSystem.includes(status)) {
			// 出勤時間
			if (!startTime) {
				showError('correctionStartTime', '出勤時間は必須です。');
				isValid = false;
			} else if (!/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(startTime)) {
				showError('correctionStartTime', 'HH:mm形式で入力して下さい。');
				isValid = false;
			}

			// 退勤時間
			if (!endTime) {
				showError('correctionEndTime', '退勤時間は必須です。');
				isValid = false;
			} else if (!/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(endTime)) {
				showError('correctionEndTime', 'HH:mm形式で入力して下さい。');
				isValid = false;
			}

			// 出勤時間と退勤時間の比較
			if (startTime && endTime) {
				const startTimeInMinutes = parseInt(startTime.split(':')[0]) * 60 + parseInt(startTime.split(':')[1]);
				const endTimeInMinutes = parseInt(endTime.split(':')[0]) * 60 + parseInt(endTime.split(':')[1]);

				if (startTimeInMinutes >= endTimeInMinutes) {
					showError('correctionStartTime', '出勤時間は退勤時間より先になるように入力して下さい。');
					showError('correctionEndTime', '退勤時間は出勤時間より後になるように入力して下さい。');
					isValid = false;
				}
			}
		}

		// 備考
		if (remarks.length > MAX_REMARKS_LENGTH) {
			showError('correctionRemarks', '備考は' + MAX_REMARKS_LENGTH + '字以内で入力して下さい。');
			isValid = false;
			//		} else if (!/^[^\x00-\x7F]*$/.test(remarks)) {
			//			showError('correctionRemarks', '全角で入力して下さい。');
			//			isValid = false;
		}

		// 訂正理由
		if (!correctionReason) {
			showError('correctionCorrectionReason', '訂正理由を入力して下さい。');
			isValid = false;
		} else if (correctionReason.length > MAX_REASON_LENGTH) {
			showError('correctionCorrectionReason', '訂正理由は' + MAX_REASON_LENGTH + '字以内で入力して下さい。');
			isValid = false;
		}

		// すべてのバリデーションが成功した場合
		if (isValid) {
			const correctionForm = {
				userId: document.getElementById('correction-userId').value,
				date: document.getElementById("correction-date").value.replace(/\//g, '-'), // yyyy/MM/ddからyyyy-MM-ddに変換
				startTime,
				endTime,
				remarks,
				correctionReason,
				status,
				rejectFlg: document.getElementById('correction-regect-flg').value || 0,
				applicationDate: new Date().toISOString().split('T')[0] // YYYY-MM-DD形式
			};

			fetch("/attendance/correction", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(correctionForm),
			})
				.then(response => {
					if (!response.ok) {
						throw new Error("Network response was not ok: " + response.status);
					}
					return response.json();
				})
				.then(data => {
					console.log(data); // 受信したデータを表示
					if (data.success) {
						localStorage.setItem('message', data.message);
						window.location.href = "/attendance/regist/display?year=" + data.year + "&month=" + data.month;
					} else {
						document.getElementById("correctionStartTimeError").innerText = data.message;
						document.getElementById("correctionEndTimeError").innerText = data.message;
						document.getElementById("correctionRemarksError").innerText = data.message;
						document.getElementById("correctionCorrectionReasonError").innerText = data.message;
						document.getElementById("correctionStatusError").innerText = data.message;
					}
				})
				.catch(error => {
					console.error("Error:", error);
				});
		}
	});
});



// 【却下モーダル】モーダルの表示、「却下」ボタン押下でフォーム送信
document.addEventListener('DOMContentLoaded', function() {
	const rejectModalBtn = document.getElementById('rejectModalBtn');
	const rejectModal = new bootstrap.Modal(document.getElementById('rejectModal'));
	const rejectBtn = document.getElementById('reject-btn');
	const rejectForm = document.getElementById('reject-form');
	const rejectWarning = document.getElementById("rejectWarning");
	const modalErrorMessage = document.getElementById("modalErrorMessage");
	// 月次
	const commentReasonInput = document.getElementById("commentReasonInput");
	const rejectCommentError = document.getElementById("rejectCommentError");
	// 訂正
	const rejectionReasonInput = document.getElementById("rejectionReasonInput");
	const rejectionReasonError = document.getElementById("rejectionReasonError");

	// モーダル表示
	if (rejectModalBtn) {
		rejectModalBtn.addEventListener('click', function() {
			rejectModal.show();
		});
	}

	// フォーム送信
	rejectBtn.addEventListener('click', function() {
		rejectForm.submit();
	});

	// 入力チェック エラー時にモーダル再表示
	if (document.querySelector("[data-open-modal]")) {
		const openModal = document.querySelector("[data-open-modal]").dataset.openModal;
		const modalError = document.querySelector("[data-modal-error]").dataset.modalError;

		// rejectionReasonErrorやcommentReasonErrorのエラーメッセージを表示
		// 月次
		if (rejectCommentError) {
			rejectCommentError.innerHTML = errorMessages['comment'] ?? '';
		}
		// 訂正
		if (rejectionReasonError) {
			rejectionReasonError.innerHTML = errorMessages['rejectionReason'] ?? '';
		}

		// モーダル再表示時の処理
		if (openModal === "true") {
			// 月次
			if (commentReasonInput) {
				const commentValue = commentReasonInput.value;
				commentReasonInput.value = commentValue;
			}
			// 訂正
			if (rejectionReasonInput) {
				const rejectionReasonValue = rejectionReasonInput.value;
				rejectionReasonInput.value = rejectionReasonValue;
			}

			// モーダル表示
			rejectModal.show();

			// モーダルエラーメッセージを表示
			modalErrorMessage.textContent = modalError;

			// 警告メッセージを非表示にする
			rejectWarning.style.display = "none";
		}
	}
});




//// 【承認ボタン】月次/訂正のformaction動的変更
//document.addEventListener('DOMContentLoaded', function() {
//	const approveBtn = document.getElementById('approve-btn');
//	const approveForm = document.getElementById('approve-form');
//
//	if (approveBtn) {
//		approveBtn.addEventListener('click', function() {
//			let formAction;
//
//			if (window.location.pathname === '/attendance/approveRequests') {
//				formAction = '/attendance/approveMonthly';
//			} else if (window.location.pathname === '/attendance/correctionRequests') {
//				formAction = '/attendance/approveCorrection';
//			}
//
//			approveForm.action = formAction;
//			approveForm.submit();
//		});
//	}
//});
// 【承認ボタン】月次/訂正のformaction動的変更
function setAction(action) {
	document.getElementById('form-action').value = action;
}

//土日祝に色を付ける、今日の日付の行に色を付ける（社員権限）
document.addEventListener('DOMContentLoaded', function() {

	// 今日の日付を取得 yyyy/MM/dd
	const date = new Date();
	const today = date.toLocaleDateString('ja-JP', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit'
	});

	// 行全体を取得
	const rows = document.querySelectorAll('.attendance-row');

	rows.forEach(row => {
		// 行の日付を取得
		const rowDate = row.getAttribute('data-date');
		let rowStatus = row.getAttribute('data-status');

		// 行の曜日を取得
		const weekdayCell = row.querySelector('.weekdayCells');
		const weekdayCellText = weekdayCell.textContent.trim();

		// 祝日(優先)、土日に色を付ける
		if (holidays.includes(rowDate)) {
			weekdayCell.style.color = 'red';
		}
		else if (weekdayCellText === '土') {
			weekdayCell.style.color = 'rgb(135 135 255)';
		}
		else if (weekdayCellText === '日') {
			weekdayCell.style.color = 'rgb(255, 93, 93)';
		}

		//勤怠状況の土日祝日のプルダウンを設定
		let status = row.querySelector('.attendance-status');
		if (status !== null) {
			const weekendStatus = "2";
			const holidayStatus = "3";
			if (rowStatus == null) {

				if (holidays.includes(rowDate)) {
					status[holidayStatus].selected = true;
				}
				if (weekdayCellText === '土' || weekdayCellText === '日') {
					status[weekendStatus].selected = true;
				}
			}
		}

		// 今日の日付の行に色を付ける
		if (rowDate === today) {
			row.style.backgroundColor = 'rgb(255, 255, 225)';
		}
	});
});


//// 土日祝に色を付ける（社員権限）
//document.addEventListener('DOMContentLoaded', function() {
//    // すべての曜日セルを取得
//    const weekCells = document.querySelectorAll('.weekdayCells');
//
//    weekCells.forEach(cell => {
//        const dayOfWeek = cell.textContent.trim();
//
//        if (dayOfWeek === '土') {
//            cell.style.color = 'rgb(135 135 255)';  // 土曜日を青色に
//        } else if (dayOfWeek === '日') {
//            cell.style.color = 'rgb(255, 93, 93)';   // 日曜日を赤色に
//        }
//    });
//});
//
//// 今日の日付の行に色を付ける（社員権限）
//document.addEventListener('DOMContentLoaded', function() {
//	// 今日の日付を取得 yyyy/MM/dd形式
//	const date = new Date();
//	const today = date.toLocaleDateString('ja-JP', {
//		year: 'numeric',
//		month: '2-digit',
//		day: '2-digit'
//	});
//
//	const rows = document.querySelectorAll('.attendance-row');
//	rows.forEach(row => {
//		// 行の日付を取得
//		const rowDate = row.getAttribute('data-date');
//
//		// 今日の日付と一致する場合
//		if (rowDate === today) {
//			row.classList.add('highlight');
//		}
//
////		// 祝日だったら色付ける処理
////		holidays.forEach(holiday => {
////			if (holiday === rowDate) {
////				row.classList.add('highlight');
////			}
////		})
//	});
//});




//// 入力内容をローカルストレージに保存
//document.querySelector('#display-form').addEventListener('submit', function(event) {
//	var year = document.getElementById('id_year').value;
//	var month = document.getElementById('id_month').value;
//
//	// 月を2桁にフォーマット
//	if (month.length < 2) {
//		month = '0' + month;
//	}
//
//	var yearMonth = year + '-' + month;
//
//	var hiddenInput = document.getElementById('yearMonth');
//	hiddenInput.value = yearMonth;
//
//	sessionStorage.setItem('year', year);
//	sessionStorage.setItem('month', month);
//	sessionStorage.setItem('yearMonth', yearMonth);
//});
//
//// ページ読み込み時にローカルストレージから値を取得して設定
//window.addEventListener('load', function() {
//	var storedYear = sessionStorage.getItem('year');
//	var storedMonth = sessionStorage.getItem('month');
//	var storedYearMonth = localStorage.getItem('yearMonth');
//
//
//	if (storedYear) {
//		document.getElementById('id_year').value = storedYear;
//	}
//	if (storedMonth) {
//		// 月を2桁にフォーマットする
//		if (storedMonth.length < 2) {
//			storedMonth = '0' + storedMonth;
//		}
//		document.getElementById('id_month').value = storedMonth;
//	}
//	if (storedYearMonth) {
//		var hiddenInput = document.getElementById('yearMonth');
//		if (hiddenInput) {
//			hiddenInput.value = storedYearMonth;
//		}
//	}
//});

// 戻るボタン
function goBack() {
	window.history.back();
}

//『<』ボタン押下後前月へ遷移
function prevMonth(year, month) {
	let newMonth = month - 1;
	let newYear = year;
	if (newMonth < 1) {
		newYear = newYear - 1;
		newMonth = 12;
	}
	submitYearMonth(newYear, newMonth)
}
//『>』ボタン押下後翌月へ遷移
function nextMonth(year, month) {
	let newMonth = month + 1;
	let newYear = year;
	if (newMonth > 12) {
		newYear = newYear + 1;
		newMonth = 1;
	}
	submitYearMonth(newYear, newMonth)
}
//HTMLの対象年月日を上書きして表示ボタン押下
function submitYearMonth(newYear, newMonth) {
	document.getElementById('id_year').value = newYear;
	document.getElementById('id_month').value = newMonth;
	document.getElementById('display-form').submit();
}