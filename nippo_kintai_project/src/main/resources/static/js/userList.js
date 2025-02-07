//詳細検索ボタン押下で詳細検索カード表示/非表示
const detailSearchBtn = document.getElementById('detailSearchBtn');

if (detailSearchBtn) {
	detailSearchBtn.addEventListener('click', function() {
		const detailSearchCard = document.getElementById('detailSearchCard');
		const icon = this.querySelector('i');

		if (detailSearchCard) { 
			if (detailSearchCard.style.display == 'none' || detailSearchCard.style.display === '') {
				detailSearchCard.style.display = 'block';
				icon.className = 'bi bi-chevron-up';
			} else {
				detailSearchCard.style.display = 'none';
				icon.className = 'bi bi-chevron-down';
			}
		} else {
			console.log('detailSearchCard 要素が見つかりません');
		}
	});
}

//ボタン押下で画面上部に遷移
const pageTopBtn = document.getElementById('pageTopBtn');
if (pageTopBtn) {
	pageTopBtn.addEventListener('click', () => {
		window.scroll({
			top: 0,
			behavaior: "smoth",
		});
	});
}

//リセットボタン
const resetBtn = document.getElementById('resetBtn');
if (resetBtn) {
	resetBtn.addEventListener('click', function() {
		location.href = '/user/list';
	});
}

//テーブルのソート機能
document.addEventListener('DOMContentLoaded', function() {
	const table = document.getElementById('users-table');
	const headers = table.querySelectorAll('th');

	//アイコン
	const SORT_ICON = '<i class="fa-solid fa-sort"></i>';
	const DESC_ICON = '<i class="fa-solid fa-sort-up"></i>';
	const ASC_ICON = '<i class="fa-solid fa-sort-down"></i>';

	headers.forEach((header, columnIndex) => {
		if (columnIndex === headers.length - 1) return; //編集ボタンはソート不要

		header.style.cursor = 'pointer';

		//ソートアイコンの要素を作成
		const sortIcon = document.createElement('span');
		sortIcon.innerHTML = SORT_ICON;
		sortIcon.classList.add('sort-icon');
		header.appendChild(sortIcon);
		header.dataset.order = ''; //初期状態は未ソート

		header.addEventListener('click', function() {
			const tbody = table.querySelector('tbody');
			const rows = Array.from(tbody.querySelectorAll('tr'));

			//すべてのヘッダーのアイコンをリセット
			headers.forEach((h, i) => {
				const icon = h.querySelector('span');
				if (icon) {
					if (i === columnIndex) {
					} else {
						icon.innerHTML = SORT_ICON;
					}
				}
			});

			//dataset.orderの切り替え
			const isAsc = header.dataset.order !== 'asc';
			header.dataset.order = isAsc ? 'asc' : 'desc';
			//アイコンの切り替え
			sortIcon.innerHTML = isAsc ? ASC_ICON : DESC_ICON;
			//並び替え処理
			rows.sort((rowA, rowB) => {
				const cellA = rowA.children[columnIndex].textContent.trim();
				const cellB = rowB.children[columnIndex].textContent.trim();
				return isAsc ? cellA.localeCompare(cellB, 'ja') : cellB.localeCompare(cellA, 'ja');
			});
			//並び替えたデータを反映
			tbody.append(...rows);
		});
	});
});


//ファイル選択後文言変更
document.addEventListener('DOMContentLoaded', function() {
	const fileInput = document.getElementById('fileUpload');
	const fileNameDisplay = document.getElementById('fileName');
	const uploadBtn = document.getElementById('uploadBtn');

	if (fileInput && fileNameDisplay && uploadBtn) {
		fileInput.addEventListener('change', function() {
			const file = fileInput.files[0];
			if (file) {
				fileNameDisplay.textContent = `選択中 : ${file.name}`;
				uploadBtn.classList.remove('disabled');
			} else {
				fileNameDisplay.textContent = 'ファイルが選択されていません';
				uploadBtn.classList.add('disabled');
			}
		});

	} else {
		console.log('必要な要素が見つかりませんでした');
	}
});

/**
 * CSV読み込み一覧フォームモーダル
 */
window.addEventListener("DOMContentLoaded", (event) => {
	// サーバからのエラー情報を取得
	if (document.querySelector("[data-open-modal]")) {
		const openModal = document.querySelector("[data-open-modal]").dataset.openModal;
		if (openModal === "true") {
			// モーダルを表示
			var csvInportModal = new bootstrap.Modal(document.getElementById('usersModal'));
			csvInportModal.show();
		}
	}
});

// チェックされた行のデータを取得するJavaScriptの例
function getSelectedRows(inputcontent) {
	const selectedRows = [];
	document.querySelectorAll(inputcontent).forEach(checkbox => {
		const row = checkbox.closest('tr'); // チェックボックスの親行
		console.log(row);
		const rowData = {
			employeeCode: row.querySelector('.employeeCode').textContent,
			name: row.querySelector('.name').textContent,
			role: row.querySelector('.role').textContent,
			departmentId: row.querySelector('.departmentId').textContent,
			email: row.querySelector('.email').textContent,
			startDate: row.querySelector('.startDate').textContent,
		};
		selectedRows.push(rowData);
	});
	return selectedRows;
}
//『インポート』ボタン押下後
document.getElementById("selective-import").addEventListener("click", (event) => {
	const selectedRows = getSelectedRows('input[type="checkbox"]:checked');
	sendInportData(selectedRows);
});
//『全件インポート』ボタン押下後
document.getElementById("all-import").addEventListener("click", (event) => {
	const selectedRows = getSelectedRows('input[type="checkbox"]');
	sendInportData(selectedRows);
});
//インポート開始
function sendInportData(selectedRows) {
	if (selectedRows.length > 0) {
		fetch('/user/regist/import/complete', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(selectedRows)
		}).then(response => {

			if (response.ok) {
				console.log('インポート成功')
			} else {
				console.log('インポート失敗');
			}
			return response.json();
		}).then(data => {
			console.dir(data);
			console.log(data.message);
			document.getElementById(`${data.status}`).style.display = '';
			document.getElementById(`${data.status}`).innerHTML = data.message;

		})
	} else {
		let csvInportModal = new bootstrap.Modal(document.getElementById('usersModal'));
		csvInportModal.show();
		document.getElementById('modalErrorMessage').style.display = '';
		document.getElementById('modalErrorMessage').innerHTML = 'インポートする行を選択してください';
	}
};
//ファイル選択後
let importFile = document.getElementById('import-file');
importFile.addEventListener('input', function(event) {
	let importSubmit = document.getElementById('importSubmit-btn');
	const fileName = importFile.value
	const allowedExtensions = ['csv'];
	const fileExtension = fileName.split('.').pop().toLowerCase();
	if (!allowedExtensions.includes(fileExtension)) {
		document.getElementById('error').style.display = '';
		document.getElementById('error').innerHTML = '対応していないファイル形式です。csv形式のファイルを選択してください。';
		return;
	}
	importSubmit.submit();
});
