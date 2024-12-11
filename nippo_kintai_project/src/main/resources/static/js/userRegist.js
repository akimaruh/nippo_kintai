//検索フォームのバリデーションチェック
//document.getElementById('search-form').addEventListener('submit', function(event) {
//
//	let searchEmployeeCode = document.getElementById('searchEmployeeCode').value;
//	const SEARCH_EMPLOYEECODE_LENGTH = 10;
//	let validation = true;
//
//	if (searchEmployeeCode === "") {
//		document.getElementById('searchEmployeeCodeError').innerHTML = "社員番号を入力してください";
//		validation = false;
//	} else if (searchEmployeeCode.length >= SEARCH_EMPLOYEECODE_LENGTH) {
//		document.getElementById('searchEmployeeCodeError').innerHTML = "10桁以内で入力してください";
//		validation = false;
//	} else if (!/^[0-9]+$/.test(searchEmployeeCode)) {
//		document.getElementById('searchEmployeeCodeError').innerHTML = "数字のみ入力可能です";
//		validation = false;
//	} else {
//		document.getElementById('searchEmployeeCodeError').innerHTML = "";
//	} if (!validation) {
//		event.preventDefault();
//
//	}
//
//});

////検索フォーム内の社員番号を登録フォームにコピー
//document.getElementById('regist-form').addEventListener('submit', function(event) {
//	const employeeCode = document.getElementById('employeeCode').value;
//	document.getElementById('hidden-employeeCode').value = employeeCode;
//	return true;
//});

// 登録フォームのバリデーションチェック
document.getElementById('regist-form').addEventListener('submit', function(event) {

	let name = document.getElementById('name').value;
	let employeeCode = document.getElementById('employee').value;
	//	let password = document.getElementById('password').value;
	let role = document.getElementById('role').value;
	let departmentId = document.getElementById('departmentId').value;
	let email = document.getElementById('email').value;
	let startDate = document.getElementById('startDate').value;
	const commandDate = "9999/99/99";
	let date = new Date(startDate);

	const MAX_USERNAME_LENGTH = 20;
	//	const MAX_PASSWORD_LENGTH = 16;
	const STARTDATE_LENGTH = 10;
	const EMPLOYEECODE_LENGTH = 10;
	let validation = true;

	if (name === "") {
		document.getElementById('nameError').innerHTML = "名前を入力してください";
		validation = false;
	} else if (name.length >= MAX_USERNAME_LENGTH) {
		document.getElementById('nameError').innerHTML = "20文字以内で入力してください";
		validation = false;
	} else if (!/^[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFFa-zA-Z]+$/.test(name)) {
		document.getElementById('nameError').innerHTML = "漢字、全角カタカナ、半角英字のみ入力可能です";
		validation = false;
	} else {
		document.getElementById('nameError').innerHTML = "";
	}

	if (employeeCode === "") {
		document.getElementById('employeeCodeError').innerHTML = "社員番号を入力してください";
		validation = false;
	} else if (employeeCode.length >= EMPLOYEECODE_LENGTH) {
		document.getElementById('employeeError').innerHTML = "10桁以内で入力してください";
		validation = false;
	} else if (!/^[0-9]+$/.test(employeeCode)) {
		document.getElementById('employeeError').innerHTML = "数字のみ入力可能です";
		validation = false;
	} else {
		document.getElementById('employeeError').innerHTML = "";
	}

	//	if (password === "") {
	//		document.getElementById('passwordError').innerHTML = "パスワードを入力してください";
	//		validation = false;
	//	} else if (password.length > MAX_PASSWORD_LENGTH) {
	//		document.getElementById('passwordError').innerHTML = "16文字以内で入力してください";
	//		validation = false;
	//	} else if (!/^[0-9a-zA-Z\-\s]*$/.test(password)) {
	//		document.getElementById('passwordError').innerHTML = "半角で入力してください";
	//		validation = false;
	//	} else {
	//		document.getElementById('passwordError').innerHTML = "";
	//	}

	if (role === "") {
		document.getElementById('roleError').innerHTML = "権限を選択してください";
		validation = false;
	} else {
		document.getElementById('roleError').innerHTML = "";
	}
	if (departmentId === "") {
		document.getElementById('departmentIdError').innerHTML = "所属部署を選択してください";
		validation = false;
	} else {
		document.getElementById('departmentIdError').innerHTML = "";
	}
	if (!email.includes("@")) {
		document.getElementById('startDateError').innerHTML = "メールアドレスが不正です";
		validation = false;
	} else {
		document.getElementById('startDateError').innerHTML = "";
	}

	if (startDate === "") {
		document.getElementById('startDateError').innerHTML = "利用開始日を入力してください";
		validation = false;
	} else if (startDate.length != STARTDATE_LENGTH) {
		document.getElementById('startDateError').innerHTML = "10文字で入力してください";
		validation = false;
	} else if (startDate != commandDate && isNaN(date.getDate())) {
		document.getElementById('startDateError').innerHTML = "利用開始日が不正です";
		validation = false;
	} else {
		document.getElementById('startDateError').innerHTML = "";
	}

	if (!validation) {
		event.preventDefault();

	}


});

// パスワード表示・非表示	
//window.addEventListener('DOMContentLoaded', function() {
//	let btn_passview = document.getElementById("btn_passview");
//	let passwordInput = document.getElementById("password");
//	let viewIcon = document.getElementById("view");
//
//	passwordInput.type = 'password'; //初期状態はパスワードが隠れている状態
//	passwordInput.style.display = ''; // スタイルをリセットして表示する
//
//	btn_passview.addEventListener("click", function() {
//		if (passwordInput.type === 'password') {
//			passwordInput.type = 'text'; //表示
//			viewIcon.classList.remove('fa-eye-slash');
//			viewIcon.classList.add('fa-eye');
//
//		} else {
//			passwordInput.type = 'password'; //非表示
//			viewIcon.classList.remove('fa-eye');
//			viewIcon.classList.add('fa-eye-slash');
//		}
//	});
//});

//日付の入力補助機能
document.getElementById('startDate').addEventListener('blur', function() {
	let input = document.getElementById('startDate').value;
	console.log(input);
	// 「9999/99/99」は変更しない
	if (input === "9999/99/99") return;

	// (yyyy/MM/dd,yyyy-MM-dd,yyyyMMdd)入力パターンに対応
	input = input.replace(/-/g, '/');  // ハイフンをスラッシュに変換
	let regex = /^(\d{2,4})[\/\-]?(\d{1,2})[\/\-]?(\d{1,2})$/;

	let match = input.match(regex);
	if (match) {
		let year = match[1];
		let month = match[2];
		let day = match[3];

		// 年が2桁の場合は20xxとして扱う
		if (year.length === 2) {
			year = '20' + year;
		}

		// 月や日の桁数が1桁の場合は0埋めする
		month = month.padStart(2, '0');
		day = day.padStart(2, '0');

		// 日付の補正
		const date = new Date(year, month - 1, day);
		//		const currentDate = new Date();

		//		// 入力日が2年以内であることを確認
		//		const twoYearsLater = new Date();
		//		twoYearsLater.setFullYear(currentDate.getFullYear() +2);

		//		if (date >= currentDate && date <= twoYearsLater) {
		// 補正された日付を「yyyy/MM/dd」形式に修正
		const formattedDate = `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')}`;
		input = formattedDate;
		//		}
		console.log(input);
		document.getElementById('startDate').value = input;
	}
});

// テキストボックスのレスポンシブ調整
function adjustContainerClass() {
	const containers = document.querySelectorAll('.responsiveContainer');

	containers.forEach(container => {
		if (window.innerWidth < 576) {
			container.classList.remove('container'); // スマホサイズでは削除
		} else {
			container.classList.add('container'); // 大きいサイズでは追加
		}
	});
}

adjustContainerClass();
window.addEventListener('resize', adjustContainerClass);


// ボタン押下で画面上部に遷移
const pageTopBtn = document.getElementById('pageTopBtn');
pageTopBtn.addEventListener('click', () => {
	window.scroll({
		top: 0,
		behavaior: "smoth",
	});
});


//↓元々書いてあったやつ
////バリデーションチェック
//document.getElementById('search-form').addEventListener('submit',function(event){
//  //バリデーションチェック
//
//// 変数の定義
//var halfwidthCheck =/^[0-9a-zA-Z\-\s]*$/
//let name = document.getElementById('search-name').value;
//let password = document.getElementById('password').value;
//let role = document.getElementById('role').value;
//let startDate = document.getElementById('startDate').value;
//
//const MAX_USERNAME_LENGTH = 20;
//const MAX_PASSWORD_LENGTH = 16;
//const MAX_STARTDATE_LENGTH = 10;
//
//let validation = true;
//
//  if(name === ""){
//    document.getElementById('nameError').innerHTML = "名前を入力して下さい";
//    validation = false;
//  } if(name.length >MAX_USERNAME_LENGTH){
//	  document.getElementById('nameError').innerHTML = "10文字以内で入力して下さい";
//    validation = false;
//
//  }else{
//	  document.getElementById('nameError').innerHTML = "";
//  }
//
//
//   if(password === ""){
//    document.getElementById('password').innerHTML = "パスワードを入力して下さい";
//    validation = false;
//   } if(password.length >MAX_PASSWORD_LENGTH){
//	  document.getElementById('password').innerHTML = "16文字以内で入力して下さい";
//    validation = false;
//
//    }if(!halfwidthCheck.test(password)){
//		document.getElementById('password').innerHTML = "半角で入力して下さい";
//
//
//  }else{
//	  document.getElementById('password').innerHTML = "";
//  }
//
//
//
//  if(role === ""){
//    document.getElementById('role').innerHTML = "権限を選択して下さい";
//    validation = false;
//  } else{
//    document.getElementById('role').innerHTML = "";
//  }
//
//
//  if(startDate === ""){
//    document.getElementById('startDate').innerHTML = "利用開始日を入力して下さい";
//    validation = false;
// } if(startDate.length >MAX_STARTDATE_LENGTH){
//	  document.getElementById('startDate').innerHTML = "10文字以内で入力して下さい";
//    validation = false;
//
//  }else{
//	  document.getElementById('startDate').innerHTML = "";
//  }
//
//
////  if(!check.test(email)){
////    document.getElementById('message1').innerHTML = "メールアドレスの形式で入力して下さい";
////    validation = false;
////  }else{
////    document.getElementById('message1').innerHTML = "";
////  }
//
//  // バリデーションに引っかかった場合は送信させない
//  if(!validation){
//    event.preventDefault();
//  }
//});