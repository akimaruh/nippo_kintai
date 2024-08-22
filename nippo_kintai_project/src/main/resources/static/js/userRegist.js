//バリデーションチェック
document.getElementById('search-form').addEventListener('submit',function(event){
  //バリデーションチェック

// 変数の定義
var halfwidthCheck =/^[0-9a-zA-Z\-\s]*$/
let name = document.getElementById('search-name').value;
let password = document.getElementById('password').value;
let role = document.getElementById('role').value;
let startDate = document.getElementById('startDate').value;

const MAX_USERNAME_LENGTH = 20;
const MAX_PASSWORD_LENGTH = 16;
const MAX_STARTDATE_LENGTH = 10;

let validation = true;

  if(name === ""){
    document.getElementById('nameError').innerHTML = "名前を入力して下さい";
    validation = false;
  } if(name.length >MAX_USERNAME_LENGTH){
	  document.getElementById('nameError').innerHTML = "10文字以内で入力して下さい";
    validation = false;
    
  }else{
	  document.getElementById('nameError').innerHTML = "";
  }
  
  
   if(password === ""){
    document.getElementById('password').innerHTML = "パスワードを入力して下さい";
    validation = false;
   } if(password.length >MAX_PASSWORD_LENGTH){
	  document.getElementById('password').innerHTML = "16文字以内で入力して下さい";
    validation = false;
    
    }if(!halfwidthCheck.test(password)){
		document.getElementById('password').innerHTML = "半角で入力して下さい";
	
    
  }else{
	  document.getElementById('password').innerHTML = "";
  }
    
    
  
  if(role === ""){
    document.getElementById('role').innerHTML = "権限を選択して下さい";
    validation = false;
  } else{
    document.getElementById('role').innerHTML = "";
  }
  
  
  if(startDate === ""){
    document.getElementById('startDate').innerHTML = "利用開始日を入力して下さい";
    validation = false;
 } if(startDate.length >MAX_STARTDATE_LENGTH){
	  document.getElementById('startDate').innerHTML = "10文字以内で入力して下さい";
    validation = false;
    
  }else{
	  document.getElementById('startDate').innerHTML = "";
  }
  

//  if(!check.test(email)){
//    document.getElementById('message1').innerHTML = "メールアドレスの形式で入力して下さい";
//    validation = false;
//  }else{
//    document.getElementById('message1').innerHTML = "";
//  }

  // バリデーションに引っかかった場合は送信させない
  if(!validation){
    event.preventDefault();
  }
});