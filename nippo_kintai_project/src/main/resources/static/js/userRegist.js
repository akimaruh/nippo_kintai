//検索フォームのバリデーションチェック
document.getElementById('search-form').addEventListener('submit', function(event) {

	let name = document.getElementById('name').value;
	const MAX_USERNAME_LENGTH = 20;
	const regex = /^[^\\x00-\\x7F]*$/;

	let validation = true;

	if (name === "") {
		document.getElementById('nameError').innerHTML = "名前を入力してください";
		validation = false;
	} else if (name.length > MAX_USERNAME_LENGTH) {
		document.getElementById('nameError').innerHTML = "全角20文字以内で入力してください";
		validation = false;
	} else if (/^[0-9a-zA-Zｱ-ﾝ\-\s!-/:-@[-`{-~]*$/.test(name)) {
		document.getElementById('nameError').innerHTML = "全角で入力してください";
		validation = false;
	} else {
		document.getElementById('nameError').innerHTML = "";
	}
	if (!validation) {
		event.preventDefault();
		validation = false;
	}

});

//検索フォーム内のユーザー名を登録フォームにコピー
document.getElementById('regist-form').addEventListener('submit', function(event) {
	const username = document.getElementById('name').value;
	document.getElementById('hidden-name').value = username;
	return true;
});

// 登録フォームのバリデーションチェック
document.getElementById('regist-form').addEventListener('submit', function(event) {

	let name = document.getElementById('name').value;
	let password = document.getElementById('password').value;
	let role = document.getElementById('role').value;
	let departmentId = document.getElementById('departmentId').value;
	let startDate = document.getElementById('startDate').value;
	const commandDate = "9999/99/99";
	let date = new Date(startDate);

	const regex = /^[\\x00-\\x7F]*$/;

	const MAX_USERNAME_LENGTH = 20;
	const MAX_PASSWORD_LENGTH = 16;
	const STARTDATE_LENGTH = 10;

	let validation = true;

	if (name === "") {
		document.getElementById('nameError').innerHTML = "名前を入力してください";
		validation = false;
	} else if (name.length > MAX_USERNAME_LENGTH) {
		document.getElementById('nameError').innerHTML = "全角20文字以内で入力してください";
		validation = false;
	} else if (/^[0-9a-zA-Zｱ-ﾝ\-\s!-/:-@[-`{-~]*$/.test(name)) {
		document.getElementById('nameError').innerHTML = "全角で入力してください";
		validation = false;
	} else {
		document.getElementById('nameError').innerHTML = "";
	}

	if (password === "") {
		document.getElementById('passwordError').innerHTML = "パスワードを入力してください";
		validation = false;
	} else if (password.length > MAX_PASSWORD_LENGTH) {
		document.getElementById('passwordError').innerHTML = "16文字以内で入力してください";
		validation = false;
	} else if (!/^[0-9a-zA-Z\-\s]*$/.test(password)) {
		document.getElementById('passwordError').innerHTML = "半角で入力してください";
		validation = false;
	} else {
		document.getElementById('passwordError').innerHTML = "";
	}

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
window.addEventListener('DOMContentLoaded', function() {
	let btn_passview = document.getElementById("btn_passview");
	let passwordInput = document.getElementById("password");
	let viewIcon = document.getElementById("view");

	passwordInput.type = 'password'; //初期状態はパスワードが隠れている状態
	passwordInput.style.display = ''; // スタイルをリセットして表示する

	btn_passview.addEventListener("click", function() {
		if (passwordInput.type === 'password') {
			passwordInput.type = 'text'; //表示
			viewIcon.classList.remove('fa-eye-slash');
			viewIcon.classList.add('fa-eye');

		} else {
			passwordInput.type = 'password'; //非表示
			viewIcon.classList.remove('fa-eye');
			viewIcon.classList.add('fa-eye-slash');
		}
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